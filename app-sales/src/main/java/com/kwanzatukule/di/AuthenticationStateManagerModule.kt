package com.kwanzatukule.di

import com.kwanzatukule.RootComponent
import com.kwanzatukule.features.core.domain.AuthenticationStateManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@Module
@InstallIn(ActivityComponent::class)
abstract class AuthenticationStateManagerModule {
    @Binds
    abstract fun bindAuthenticationStateManager(
        manager: RootComponent,
    ): AuthenticationStateManager
}