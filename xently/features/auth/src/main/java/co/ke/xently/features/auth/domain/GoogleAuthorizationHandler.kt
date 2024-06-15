package co.ke.xently.features.auth.domain

import android.accounts.Account
import android.content.Context
import android.os.CancellationSignal
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

fun interface GoogleAuthorizationHandler {
    suspend fun handleAuthorization(): AuthorizationResult

    companion object {
        fun create(context: Context, accountId: String?): GoogleAuthorizationHandler {
            return GoogleAuthorizationHandler {
                val requestedScopes = listOf(Scopes.EMAIL, Scopes.PROFILE, Scopes.OPEN_ID)
                    .map(::Scope)

                val authorizationRequest = AuthorizationRequest.builder()
                    .setRequestedScopes(requestedScopes)
                    .setAccount(Account(accountId, "com.google"))
                    .build()

                withContext(Dispatchers.IO) {
                    suspendCancellableCoroutine { continuation ->
                        val cancellationSignal = CancellationSignal()

                        continuation.invokeOnCancellation { cancellationSignal.cancel() }

                        Identity.getAuthorizationClient(context)
                            .authorize(authorizationRequest)
                            .addOnSuccessListener {
                                if (continuation.isActive) {
                                    continuation.resume(it)
                                }
                            }
                            .addOnFailureListener {
                                if (continuation.isActive) {
                                    continuation.resumeWithException(it)
                                }
                            }
                            .addOnCanceledListener(continuation::cancel)
                    }
                }
            }
        }
    }
}