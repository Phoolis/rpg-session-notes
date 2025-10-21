package fi.paulcarlson.plugins

import fi.paulcarlson.util.UUIDSerializer
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import java.util.UUID


fun Application.configureSerialization() {
    val module = SerializersModule {
        contextual(UUID::class, UUIDSerializer) // UUIDs need a custom serializer
    }
    install(ContentNegotiation) {
      json(
          Json {
              serializersModule = module
              prettyPrint = true
              ignoreUnknownKeys = true
              isLenient = true
          }
      )
    }
}
