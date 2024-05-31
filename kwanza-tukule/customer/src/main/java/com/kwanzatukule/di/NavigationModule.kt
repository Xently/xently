package com.kwanzatukule.di

import android.content.Context
import androidx.activity.ComponentActivity
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.retainedComponent
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.scopes.ActivityScoped

@Module
@InstallIn(ActivityComponent::class)
object NavigationModule {
    @Provides
    @ActivityScoped
    fun provideComponentContext(
        @ActivityContext
        context: Context,
    ): ComponentContext {
        return (context as ComponentActivity).retainedComponent {
            it
        }
    }
}