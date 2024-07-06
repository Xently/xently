package co.ke.xently.customer.di

import android.content.Context
import androidx.room.Room
import co.ke.xently.customer.AppDatabase
import co.ke.xently.customer.BuildConfig
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
            "${context.packageName}.xently.db",
        ).fallbackToDestructiveMigration().apply {
            if (BuildConfig.DEBUG) {
                setQueryCallback(
                    { query, args ->
                        Timber.d(
                            "Query <%s> %s",
                            query,
                            args.joinToString(prefix = "Args: <", postfix = ">"),
                        )
                    },
                    Executors.newSingleThreadExecutor(),
                )
            }
        }.build()
    }
}