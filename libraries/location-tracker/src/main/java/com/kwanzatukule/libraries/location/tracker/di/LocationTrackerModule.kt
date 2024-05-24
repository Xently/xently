package com.kwanzatukule.libraries.location.tracker.di

import com.kwanzatukule.libraries.location.tracker.domain.LocationTracker
import com.kwanzatukule.libraries.location.tracker.domain.LocationTrackerImpl
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