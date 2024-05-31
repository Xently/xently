package com.kwanzatukule.features.delivery.profile.presentation

interface ProfileComponent {
    fun onSignInRequested()
    fun onSignOutRequested()

    object Fake : ProfileComponent {
        override fun onSignInRequested() {}
        override fun onSignOutRequested() {}
    }
}
