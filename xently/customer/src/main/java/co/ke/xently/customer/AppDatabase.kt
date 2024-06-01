package co.ke.xently.customer

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.withTransaction
import co.ke.xently.features.auth.data.source.AuthenticationDatabase
import co.ke.xently.features.auth.data.source.User

@Database(
    version = 1,
    entities = [
        User::class,
    ],
)
abstract class AppDatabase : RoomDatabase(),
    AuthenticationDatabase{
    override suspend fun <R> withTransactionFacade(block: suspend () -> R): R {
        return withTransaction(block)
    }
}
