package co.ke.xently.features.notifications.data.source.di

import co.ke.xently.features.notifications.data.source.NotificationRepository
import co.ke.xently.features.notifications.data.source.NotificationRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class NotificationModule {
    @Binds
    abstract fun bindsNotificationRepository(repository: NotificationRepositoryImpl): NotificationRepository
}