package co.ke.xently.features.auth.data.source

import co.ke.xently.features.auth.data.domain.GoogleUser
import co.ke.xently.features.auth.data.domain.error.DataError
import co.ke.xently.features.auth.data.domain.error.Result
import co.ke.xently.libraries.data.auth.CurrentUser
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getCurrentUser(): Flow<CurrentUser?>
    suspend fun requestPasswordReset(email: String): Result<Unit, DataError>
    suspend fun signUp(name: String, email: String, password: String): Result<Unit, DataError>
    suspend fun signInWithGoogle(user: GoogleUser): Result<Unit, DataError>
    suspend fun signInWithEmailAndPassword(email: String, password: String): Result<Unit, DataError>
    suspend fun signOut(): Result<Unit, DataError>
}
