package com.kwanzatukule.features.delivery.landing.presentation.home

interface HomeComponent {
    fun onSignInRequested()
    fun onSignOutRequested()

    object Fake : HomeComponent {
        override fun onSignInRequested() {}
        override fun onSignOutRequested() {}
    }
}
