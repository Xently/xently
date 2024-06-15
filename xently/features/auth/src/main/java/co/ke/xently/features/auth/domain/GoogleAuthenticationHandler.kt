package co.ke.xently.features.auth.domain

import android.content.Context
import androidx.credentials.CredentialManager
import co.ke.xently.features.auth.data.domain.GoogleUser
import co.ke.xently.features.auth.data.domain.error.GoogleAuthenticationError
import co.ke.xently.features.auth.data.domain.error.Result

interface GoogleAuthenticationHandler {
    suspend fun signIn(activityContext: Context): Result<GoogleUser, GoogleAuthenticationError>
    suspend fun signOut()

    companion object {
        fun create(credentialManager: CredentialManager): GoogleAuthenticationHandler =
            GoogleAuthenticationHandlerImpl(credentialManager)
    }
}
