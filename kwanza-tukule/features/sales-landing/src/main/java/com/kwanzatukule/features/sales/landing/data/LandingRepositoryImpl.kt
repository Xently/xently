package com.kwanzatukule.features.sales.landing.data

import com.kwanzatukule.features.customer.complaints.data.CustomerComplaintRepository
import com.kwanzatukule.features.customer.home.data.HomeRepository
import com.kwanzatukule.features.sales.customer.onboarding.data.CustomerOnboardingRepository
import com.kwanzatukule.features.sales.dashboard.data.SalesDashboardRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LandingRepositoryImpl @Inject constructor(
    homeRepository: HomeRepository,
    salesDashboardRepository: SalesDashboardRepository,
    customerOnboardingRepository: CustomerOnboardingRepository,
    customerComplaintRepository: CustomerComplaintRepository,
) : LandingRepository,
    HomeRepository by homeRepository,
    SalesDashboardRepository by salesDashboardRepository,
    CustomerOnboardingRepository by customerOnboardingRepository,
    CustomerComplaintRepository by customerComplaintRepository