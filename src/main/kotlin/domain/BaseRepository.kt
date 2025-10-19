package fi.paulcarlson.domain

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.v1.r2dbc.R2dbcTransaction
import org.jetbrains.exposed.v1.r2dbc.transactions.suspendTransaction

// Interface for the non-blocking r2dbc repository

interface BaseRepository {
    suspend fun <T> dbQuery(
        block: suspend R2dbcTransaction.() -> T
    ): T = withContext(Dispatchers.IO) {
        suspendTransaction {
            block()
        }
    }
}