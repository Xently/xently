package com.kwanzatukule.features.authentication.presentation.resetpassword

interface ResetPasswordComponent {
    fun handleBackPress()
    fun handleSignIn()

    object Fake : ResetPasswordComponent {
        override fun handleBackPress() {

        }

        override fun handleSignIn() {

        }
    }
}
