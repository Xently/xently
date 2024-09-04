package co.ke.xently.features.auth.data.source

import co.ke.xently.features.access.control.data.AccessControlRepository
import co.ke.xently.features.auth.data.domain.EmailAndPasswordAuthRequest
import co.ke.xently.features.auth.data.domain.GoogleAuthRequest
import co.ke.xently.features.auth.data.domain.GoogleUser
import co.ke.xently.features.auth.data.domain.SignUpRequest
import co.ke.xently.features.auth.data.domain.error.Error
import co.ke.xently.features.auth.data.domain.error.Result
import co.ke.xently.features.auth.data.domain.error.toError
import co.ke.xently.libraries.data.auth.CurrentUser
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

@Singleton
internal class UserRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val database: AuthenticationDatabase,
    private val accessControlRepository: AccessControlRepository,
) : UserRepository {
    private val userDao = database.userDao()

    override fun getCurrentUser(): Flow<CurrentUser?> {
        return userDao.findFirst().map { user ->
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

    override suspend fun requestPasswordReset(email: String): Result<Unit, Error> {
        val urlString = accessControlRepository.getAccessControl().requestPasswordResetUrl
        return getResult {
            httpClient.post(urlString)
        }
    }

    override suspend fun signUp(request: SignUpRequest): Result<Unit, Error> {
        val accessControl = accessControlRepository.getAccessControl()
        return authenticate(urlString = accessControl.emailPasswordSignUpUrl, body = request)
    }

    override suspend fun signInWithGoogle(user: GoogleUser): Result<Unit, Error> {
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
    ): Result<Unit, Error> {
        val body = EmailAndPasswordAuthRequest(
            email = email,
            password = password,
        )
        val accessControl = accessControlRepository.getAccessControl()
        return authenticate(urlString = accessControl.emailPasswordSignInUrl, body = body)
    }

    override suspend fun signOut(): Result<Unit, Error> {
        database.postSignout()
        return Result.Success(Unit)
    }

    private suspend inline fun <reified T> authenticate(
        urlString: String,
        body: T,
    ): Result<Unit, Error> {
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
                    userDao.deleteAll()
                    userDao.save(it)
                }
            }
        }
    }

    private suspend inline fun getResult(execute: () -> Unit): Result<Unit, Error> {
        return try {
            execute()
            Result.Success(Unit)
        } catch (ex: Exception) {
            if (ex is CancellationException) throw ex
            Timber.e(ex)
            Result.Failure(ex.toError())
        }
    }
}