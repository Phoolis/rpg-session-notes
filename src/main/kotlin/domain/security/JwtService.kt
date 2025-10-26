package fi.paulcarlson.domain.security

import com.auth0.jwk.JwkProvider
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import fi.paulcarlson.domain.user.User
import fi.paulcarlson.domain.user.UserService
import io.ktor.server.auth.jwt.JWTCredential
import io.ktor.server.auth.jwt.JWTPrincipal

import io.ktor.server.config.ApplicationConfig
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Base64
import java.util.Date

class JwtService(
    config: ApplicationConfig,
    private val jwkProvider: JwkProvider,
    private val userService: UserService
) {

    private val privateKeyString = config.property("privateKey").getString()
    private val issuer = config.property("issuer").getString()
    private val audience = config.property("audience").getString()

    val realm = config.property("realm").getString()

    private fun getRSAPublicKey() = jwkProvider.get("6f8856ed-9189-488f-9011-0ff4b6c08edc").publicKey as RSAPublicKey

    private fun getRSAPrivateKey(): RSAPrivateKey {
        val keySpecPKCS8 = PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyString))
        return KeyFactory.getInstance("RSA").generatePrivate(keySpecPKCS8) as RSAPrivateKey
    }

    fun createJwtToken(user: User): String =
        JWT.create()
            .withAudience(audience)
            .withIssuer(issuer)
            .withClaim("email", user.email)
            .withExpiresAt(Date(System.currentTimeMillis() + 3_600_000))
            .sign(Algorithm.RSA256(getRSAPublicKey(), getRSAPrivateKey()))

    suspend fun customValidator(
        credential: JWTCredential,
    ): JWTPrincipal? {
        val email: String? = extractEmail(credential)
        if (email.isNullOrBlank()) {
            return null
        }
        val foundUser: User? = userService.getUserByEmail(email)

        return foundUser?.let {
            if(audienceMatches(credential))
                JWTPrincipal(credential.payload)
            else
                null
        }
    }

    private fun audienceMatches(
        credential: JWTCredential,
    ): Boolean =
        credential.payload.audience.contains(audience)

    private fun extractEmail(credential: JWTCredential): String? =
        credential.payload.getClaim("email").asString()
}