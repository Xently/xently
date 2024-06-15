package co.ke.xently.features.auth.data.source

import co.ke.xently.features.access.control.data.AccessControlRepository
import co.ke.xently.features.auth.data.domain.EmailAndPasswordAuthRequest
import co.ke.xently.features.auth.data.domain.GoogleAuthRequest
import co.ke.xently.features.auth.data.domain.GoogleUser
import co.ke.xently.features.auth.data.domain.SignUpReset
import co.ke.xently.features.auth.data.domain.error.DataError
import co.ke.xently.features.auth.data.domain.error.Result
import co.ke.xently.libraries.data.auth.CurrentUser
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
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

@Singleton
internal class UserRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val database: AuthenticationDatabase,
    private val accessControlRepository: AccessControlRepository,
) : UserRepository {
    override fun getCurrentUser(): Flow<CurrentUser?> {
        return database.userDao().findFirst().map { user ->
            if (user == null) null else {
                CurrentUser(
                    id = user.id,
                    name = user.name,
                    email = user.email,
                    emailVerified = user.emailVerified,
                    profilePicUrl = user.profilePicUrl,
                )
            }
        }
    }

    override suspend fun requestPasswordReset(email: String): Result<Unit, DataError> {
        val urlString = accessControlRepository.getAccessControl().requestPasswordResetUrl
        return getResult {
            httpClient.post(urlString)
        }
    }

    override suspend fun signUp(
        name: String,
        email: String,
        password: String,
    ): Result<Unit, DataError> {
        val names = name.split("\\s+".toRegex(), limit = 1)
        val body = SignUpReset(
            emailAddress = email,
            password = password,
            firstName = names.firstOrNull()?.takeIf(String::isNotBlank),
            lastName = if (names.size > 1) names.lastOrNull()?.takeIf(String::isNotBlank) else null,
        )
        val accessControl = accessControlRepository.getAccessControl()
        return authenticate(urlString = accessControl.emailPasswordSignUpUrl, body = body)
    }

    override suspend fun signInWithGoogle(user: GoogleUser): Result<Unit, DataError> {
        val body = GoogleAuthRequest(
            idToken = user.idToken,
            accessToken = user.accessToken,
            displayName = user.displayName,
            profilePicUrl = user.profilePicUrl,
        )
        val accessControl = accessControlRepository.getAccessControl()
        return authenticate(urlString = accessControl.googleSignInUrl, body = body)
    }

    override suspend fun signInWithEmailAndPassword(
        email: String,
        password: String,
    ): Result<Unit, DataError> {
        val body = EmailAndPasswordAuthRequest(
            email = email,
            password = password,
        )
        val accessControl = accessControlRepository.getAccessControl()
        return authenticate(urlString = accessControl.emailPasswordSignInUrl, body = body)
    }

    override suspend fun signOut(): Result<Unit, DataError> {
        delay(Random.nextLong(1_000, 3_000))
        coroutineScope {
            launch(NonCancellable) {
                database.userDao().deleteAll()
            }
        }
        return Result.Success(Unit)
    }

    private suspend inline fun <reified T> authenticate(
        urlString: String,
        body: T,
    ): Result<Unit, DataError> {
        return getResult {
            httpClient.post(urlString) {
                url {
                    parameters.run {
                        set("noauth", "0")
                    }
                }
                contentType(ContentType.Application.Json)
                setBody(body)
            }.body<UserEntity>().let {
                database.withTransactionFacade {
                    database.userDao().deleteAll()
                    database.userDao().save(it)
                }
            }
        }
    }

    private inline fun getResult(execute: () -> Unit): Result<Unit, DataError> {
        return try {
            execute()
            Result.Success(Unit)
        } catch (ex: Exception) {
            if (ex is CancellationException) throw ex
            Timber.e(ex)
            Result.Failure(DataError.Network.entries.random())
        }
    }
}