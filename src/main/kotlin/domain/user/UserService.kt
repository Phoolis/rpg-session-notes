package fi.paulcarlson.domain.user

import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.NotFoundException
import java.util.UUID

class UserService(
    private val userRepository: UserRepository
) {
    suspend fun createUser(user: User): User {
        if (userRepository.findByEmail(user.email) != null)
            throw BadRequestException("User exists with email: ${user.email}")
        return userRepository.save(user)
    }

    suspend fun getUsers(): List<User> {
        return userRepository.findAll()
    }

    suspend fun getUserById(id: UUID): User? {
        return userRepository.findById(UserId(id))
    }

    suspend fun getUserByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }

    suspend fun editUser(user: User): User {
        val existing = userRepository.findById(user.id!!)
            ?: throw NotFoundException("User not found")

        if (existing.email != user.email && userRepository.findByEmail(user.email) != null)
            throw BadRequestException("Email already in use")

        val updated = existing.copy(
            username = user.username,
            email = user.email
        )

        return userRepository.update(updated)
    }

    suspend fun removeUser(id: UUID): Boolean {
        return userRepository.delete(UserId(id))
    }
}