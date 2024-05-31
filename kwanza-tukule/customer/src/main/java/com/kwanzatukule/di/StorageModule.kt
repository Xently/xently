package com.kwanzatukule.di

import android.content.Context
import androidx.room.Room
import com.kwanzatukule.AppDatabase
import com.kwanzatukule.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import timber.log.Timber
import java.util.concurrent.Executors
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object StorageModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "${context.packageName}.kwanzatukule.db",
        ).fallbackToDestructiveMigration().apply {
            if (BuildConfig.DEBUG) {
                setQueryCallback(
                    { query, args ->
                        Timber.d(
                            "Query <%s>. Args: <%s>",
                            query,
                            args.joinToString(),
                        )
                    },
                    Executors.newSingleThreadExecutor(),
                )
            }
        }.build()
    }
}