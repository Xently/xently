package com.kwanzatukule.features.customer.landing.data

import com.kwanzatukule.features.customer.home.data.HomeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LandingRepositoryImpl @Inject constructor(
    homeRepository: HomeRepository,
) : LandingRepository, HomeRepository by homeRepository