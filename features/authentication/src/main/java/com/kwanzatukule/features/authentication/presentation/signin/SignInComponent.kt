package com.kwanzatukule.features.authentication.presentation.signin

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface SignInComponent {
    val uiState: Value<SignInUIState> get() = MutableValue(SignInUIState())
    val event: Flow<SignInEvent> get() = flow { }
    fun handleBackPress()
    fun handleSignUp()
    fun handleForgotPassword()
    fun setEmail(email: String) {}
    fun setPassword(password: String) {}
    fun togglePasswordVisibility() {}
    fun signIn() {}

    open class Fake(state: SignInUIState = SignInUIState()) : SignInComponent {
        override val uiState: Value<SignInUIState> = MutableValue(state)
        override fun handleBackPress() {}
        override fun handleSignUp() {}
        override fun handleForgotPassword() {}

        companion object : Fake()
    }
}
