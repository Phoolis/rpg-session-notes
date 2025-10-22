package fi.paulcarlson.domain.user

import fi.paulcarlson.domain.BaseRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.datetime.CurrentTimestamp
import org.jetbrains.exposed.v1.r2dbc.deleteWhere
import org.jetbrains.exposed.v1.r2dbc.insertReturning
import org.jetbrains.exposed.v1.r2dbc.selectAll
import org.jetbrains.exposed.v1.r2dbc.updateReturning

interface UserRepository : BaseRepository {
    suspend fun save(user: User): User
    suspend fun findAll(): List<User>
    suspend fun findById(id: UserId): User?
    suspend fun findByEmail(email: String): User?
    suspend fun update(user: User): User
    suspend fun delete(id: UserId): Boolean
}

class DSLUserRepository : UserRepository {
    override suspend fun save(user: User): User = dbQuery {
        val insertedRow = Users.insertReturning(
            returning = Users.columns.toList()
        ) {
            it[username] = user.username
            it[email] = user.email
        }.single()

        rowToUser(insertedRow)
    }

    override suspend fun findAll(): List<User> = dbQuery {
        Users
            .selectAll()
            .map(::rowToUser)
            .toList()
    }

    override suspend fun findById(id: UserId): User? = dbQuery {
        Users
            .selectAll()
            .where { Users.id eq id.value }
            .singleOrNull()
            ?.let(::rowToUser)
    }

    override suspend fun findByEmail(email: String): User? = dbQuery {
        Users
            .selectAll()
            .where { Users.email eq email }
            .singleOrNull()
            ?.let(::rowToUser)
    }

    override suspend fun update(user: User): User = dbQuery {
        requireNotNull(user.id) { "Missing or invalid user ID" }

        val updatedRow = Users.updateReturning(
            where = { Users.id eq user.id.value },
            returning = Users.columns.toList()
        ) {
            it[username] = user.username
            it[email] = user.email
            it[modifiedAt] = CurrentTimestamp
        }.single()

        rowToUser(updatedRow)
    }

    override suspend fun delete(id: UserId): Boolean = dbQuery {
        val rowsDeleted = Users.deleteWhere { Users.id eq id.value }
        rowsDeleted == 1
    }

}