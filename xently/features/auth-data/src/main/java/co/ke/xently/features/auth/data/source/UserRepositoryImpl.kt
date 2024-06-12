package co.ke.xently.features.auth.data.source

import co.ke.xently.features.auth.data.domain.error.DataError
import co.ke.xently.features.auth.data.domain.error.Result
import co.ke.xently.libraries.data.auth.CurrentUser
import io.ktor.client.HttpClient
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds

@Singleton
internal class UserRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val database: AuthenticationDatabase,
) : UserRepository {
    override fun getCurrentUser(): Flow<CurrentUser?> {
        return database.userDao()
            .findFirst()
            .map { user ->
                if (user == null) {
                    null
                } else {
                    CurrentUser(
                        uid = user.id,
                        firstName = user.firstName,
                        lastName = user.lastName,
                        email = user.email,
                    )
                }
            }
    }

    override suspend fun requestPasswordReset(email: String): Result<Unit, DataError> {
        val duration = Random.nextLong(1_000, 5_000).milliseconds
        try {
            delay(duration)
            return Result.Success(Unit)
        } catch (ex: Exception) {
            if (ex is CancellationException) throw ex
            Timber.e(ex)
            return Result.Failure(DataError.Network.entries.random())
        }
    }

    override suspend fun signUp(
        name: String,
        email: String,
        password: String,
    ): Result<Unit, DataError> {
        val duration = Random.nextLong(1_000, 5_000).milliseconds
        try {
            delay(duration)
            database.withTransactionFacade {
                database.userDao().deleteAll()
                database.userDao().insertAll(User(1, email, password, email, null))
            }
            return Result.Success(Unit)
        } catch (ex: Exception) {
            if (ex is CancellationException) throw ex
            Timber.e(ex)
            return Result.Failure(DataError.Network.entries.random())
        }
    }

    override suspend fun signIn(email: String, password: String): Result<Unit, DataError> {
        val duration = Random.nextLong(1_000, 5_000).milliseconds
        try {
            delay(duration)
            database.withTransactionFacade {
                database.userDao().deleteAll()
                database.userDao().insertAll(User(1, email, password, email, null))
            }
            return Result.Success(Unit)
        } catch (ex: Exception) {
            if (ex is CancellationException) throw ex
            Timber.e(ex)
            return Result.Failure(DataError.Network.entries.random())
        }
    }

    override suspend fun signOut(): Result<Unit, DataError.Local> {
        delay(Random.nextLong(1_000, 3_000))
        coroutineScope {
            launch(NonCancellable) {
                database.userDao().deleteAll()
            }
        }
        return Result.Success(Unit)
    }
}