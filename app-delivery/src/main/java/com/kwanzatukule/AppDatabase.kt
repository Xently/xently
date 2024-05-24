package com.kwanzatukule

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import com.kwanzatukule.features.authentication.data.AuthenticationDatabase
import com.kwanzatukule.features.authentication.data.User

@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase(), AuthenticationDatabase {
    override suspend fun <R> withTransactionFacade(block: suspend () -> R): R {
        return withTransaction(block)
    }
}
