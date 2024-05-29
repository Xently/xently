package com.kwanzatukule.features.delivery.landing.data

import com.kwanzatukule.features.delivery.home.data.HomeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LandingRepositoryImpl @Inject constructor(
    homeRepository: HomeRepository,
) : LandingRepository, HomeRepository by homeRepository