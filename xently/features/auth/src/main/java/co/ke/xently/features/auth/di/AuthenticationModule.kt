package co.ke.xently.features.auth.di

import android.content.Context
import androidx.credentials.CredentialManager
import co.ke.xently.features.auth.domain.GoogleAuthenticationHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthenticationModule {
    @Provides
    @Singleton
    fun provideCredentialManager(@ApplicationContext context: Context): CredentialManager {
        return CredentialManager.create(context);
    }

    @Provides
    @Singleton
    fun provideGoogleAuthenticationHandler(credentialManager: CredentialManager): GoogleAuthenticationHandler {
        return GoogleAuthenticationHandler.create(credentialManager);
    }
}
