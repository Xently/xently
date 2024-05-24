package com.kwanzatukule.features.sales.landing.data

import com.kwanzatukule.features.customer.complaints.data.CustomerComplaintRepository
import com.kwanzatukule.features.customer.home.data.HomeRepository
import com.kwanzatukule.features.sales.customer.onboarding.data.CustomerOnboardingRepository
import com.kwanzatukule.features.sales.dashboard.data.SalesDashboardRepository

interface LandingRepository : HomeRepository,
    SalesDashboardRepository,
    CustomerOnboardingRepository,
    CustomerComplaintRepository