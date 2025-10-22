package fi.paulcarlson.domain.user

import fi.paulcarlson.domain.BaseRepository
import org.jetbrains.exposed.v1.r2dbc.insert
import org.jetbrains.exposed.v1.r2dbc.insertReturning

interface UserRepository : BaseRepository {
    suspend fun save(user: User): UserId
    suspend fun findAll(): List<User>
    suspend fun findById(id: UserId): User?
    suspend fun findByEmail(email: String): User?
    suspend fun update(user: User): User
    suspend fun delete(id: UserId): Boolean
}

class DSLUserRepository : UserRepository {
    override suspend fun save(user: User): UserId = dbQuery {
        val id = Users.insert {
            it[username] = user.username
            it[email] = user.email
        } get Users.id

        UserId(id)
    }

    override suspend fun findAll(): List<User> {
        TODO("Not yet implemented")
    }

    override suspend fun findById(id: UserId): User? {
        TODO("Not yet implemented")
    }

    override suspend fun findByEmail(email: String): User? {
        TODO("Not yet implemented")
    }

    override suspend fun update(user: User): User {
        TODO("Not yet implemented")
    }

    override suspend fun delete(id: UserId): Boolean {
        TODO("Not yet implemented")
    }

}