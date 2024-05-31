package co.ke.xently.libraries.location.tracker.di

import co.ke.xently.libraries.location.tracker.domain.LocationTracker
import co.ke.xently.libraries.location.tracker.domain.LocationTrackerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationTrackerModule {
    @Binds
    abstract fun bindLocationTracker(tracker: LocationTrackerImpl): LocationTracker
}