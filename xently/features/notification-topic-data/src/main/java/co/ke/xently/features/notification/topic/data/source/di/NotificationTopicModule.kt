package co.ke.xently.features.notification.topic.data.source.di

import co.ke.xently.features.notification.topic.data.source.NotificationTopicRepository
import co.ke.xently.features.notification.topic.data.source.NotificationTopicRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class NotificationTopicModule {
    @Binds
    abstract fun bindsNotificationTopicRepository(repository: NotificationTopicRepositoryImpl): NotificationTopicRepository
}