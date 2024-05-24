package com.kwanzatukule.features.sales.customer.onboarding.data

import com.kwanzatukule.libraries.data.customer.data.CustomerRepository
import com.kwanzatukule.libraries.data.route.data.RouteRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomerOnboardingRepositoryImpl @Inject constructor(
    routeRepository: RouteRepository,
    customerRepository: CustomerRepository,
) : CustomerOnboardingRepository, RouteRepository by routeRepository,
    CustomerRepository by customerRepository