package com.kwanzatukule.features.delivery.home.data

import com.kwanzatukule.features.delivery.dispatch.data.DispatchRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepositoryImpl @Inject constructor(
    dispatchRepository: DispatchRepository,
) : HomeRepository, DispatchRepository by dispatchRepository