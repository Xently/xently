package co.ke.xently.features.auth.domain

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.GetCredentialInterruptedException
import androidx.credentials.exceptions.GetCredentialUnsupportedException
import androidx.credentials.exceptions.NoCredentialException
import co.ke.xently.features.auth.data.domain.GoogleUser
import co.ke.xently.features.auth.data.domain.error.GoogleAuthenticationError
import co.ke.xently.features.auth.data.domain.error.Result
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import timber.log.Timber


internal class GoogleAuthenticationHandlerImpl(
    private val credentialManager: CredentialManager,
) : GoogleAuthenticationHandler {
    override suspend fun signIn(activityContext: Context): Result<GoogleUser, GoogleAuthenticationError> {
        return try {
            val result = credentialManager.getCredential(
                context = activityContext,
                request = getCredentialRequest(),
            )
            handleSignIn(result)
        } catch (ex: GetCredentialException) {
            Timber.tag(TAG).e(ex, "Google authentication error")

            val error = when (ex) {
                is GetCredentialCancellationException -> GoogleAuthenticationError.CANCELLED
                is GetCredentialInterruptedException -> GoogleAuthenticationError.INTERRUPTED
                is NoCredentialException -> GoogleAuthenticationError.NO_CREDENTIALS_FOUND
                is GetCredentialUnsupportedException -> GoogleAuthenticationError.UNSUPPORTED_PROVIDER
                else -> GoogleAuthenticationError.UNEXPECTED_ERROR
            }
            Result.Failure(error)
        }
    }

    override suspend fun signOut() {
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
    }

    private fun handleSignIn(result: GetCredentialResponse): Result<GoogleUser, GoogleAuthenticationError> {
        return when (val credential = result.credential) {
            is CustomCredential -> {
                if (credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    Result.Failure(GoogleAuthenticationError.UNRECOGNISED_CREDENTIAL_TYPE)
                } else {
                    try {
                        // Use googleIdTokenCredential and extract id to validate and
                        // authenticate on your server.
                        val googleIdTokenCredential =
                            GoogleIdTokenCredential.createFrom(credential.data)
                        val user = GoogleUser(
                            id = googleIdTokenCredential.id,
                            idToken = googleIdTokenCredential.idToken,
                            accessToken = null,
                            displayName = googleIdTokenCredential.displayName ?: "",
                            profilePicUrl = googleIdTokenCredential.profilePictureUri?.toString()
                        )
                        Result.Success(data = user)
                    } catch (e: GoogleIdTokenParsingException) {
                        Timber.tag(TAG).e(e, "Received an invalid google id token response")
                        Result.Failure(GoogleAuthenticationError.INVALID_GOOGLE_ID_RESPONSE)
                    }
                }
            }

            else -> Result.Failure(GoogleAuthenticationError.UNRECOGNISED_CREDENTIAL_TYPE)
        }
    }

    private fun getCredentialRequest(): GetCredentialRequest {
        return GetCredentialRequest.Builder()
            .addCredentialOption(getGoogleIdOption())
            .build()
    }

    private fun getGoogleIdOption(): GetGoogleIdOption {
        return GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setAutoSelectEnabled(true)
            .setServerClientId("613285590946-7d6rba7q7vijodq62dn63sprkqo400ld.apps.googleusercontent.com")
            .build()
    }

    companion object {
        private val TAG = GoogleAuthenticationHandler::class.java.simpleName
    }
}