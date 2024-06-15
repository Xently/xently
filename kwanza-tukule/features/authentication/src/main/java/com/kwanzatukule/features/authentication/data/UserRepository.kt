package com.kwanzatukule.features.authentication.data

import co.ke.xently.libraries.data.auth.CurrentUser
import com.kwanzatukule.features.authentication.domain.error.DataError
import com.kwanzatukule.features.authentication.domain.error.Result
import io.ktor.client.HttpClient
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds


@Singleton
class UserRepository @Inject constructor(
    private val httpClient: HttpClient,
    private val database: AuthenticationDatabase,
) {
    fun getCurrentUser(): Flow<CurrentUser?> {
        return database.userDao()
            .findFirst()
            .map { user ->
                if (user == null) {
                    null
                } else {
                    CurrentUser(
                        id = user.id,
                        email = user.email,
                        name = listOfNotNull(user.firstName, user.lastName)
                            .joinToString(" "),
                    )
                }
            }
    }

    suspend fun signIn(email: String, password: String): Result<Unit, DataError> {
        val duration = Random.nextLong(1000, 3000).milliseconds
        try {
            delay(duration)
            database.withTransactionFacade {
                database.userDao().deleteAll()
                database.userDao().insertAll(
                    UserEntity(
                        id = UUID.randomUUID().toString(),
                        firstName = email,
                        lastName = password,
                        email = email,
                        accessToken = null
                    )
                )
            }
            return Result.Success(Unit)
        } catch (ex: Exception) {
            if (ex is CancellationException) throw ex
            Timber.e(ex)
            return Result.Failure(DataError.Network.entries.random())
        }
    }

    suspend fun signOut(): Result<Unit, DataError.Local> {
        delay(Random.nextLong(1000, 3000))
        coroutineScope {
            launch(NonCancellable) {
                database.userDao().deleteAll()
            }
        }
        return Result.Success(Unit)
    }
}