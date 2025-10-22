package fi.paulcarlson.domain.user

import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.datetime.CurrentTimestamp
import org.jetbrains.exposed.v1.datetime.timestamp

object Users : Table("users") {
    val id = uuid("id").autoGenerate()

    val username = varchar("username", 50)
    val email = varchar("email", 150).uniqueIndex() // Restrict duplicates of the same email
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    val modifiedAt = timestamp("modified_at").nullable()

    override val primaryKey = PrimaryKey(id)

}

fun rowToUser(result: ResultRow): User = User(
    id = UserId(result[Users.id]),
    username = result[Users.username],
    email = result[Users.email],
    createdAt = result[Users.createdAt],
    modifiedAt = result[Users.modifiedAt]
)