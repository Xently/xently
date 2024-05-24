package com.kwanzatukule.features.authentication.presentation.signin

import com.kwanzatukule.features.authentication.presentation.UiText

sealed interface SignInEvent {
    data class Error(
        val error: UiText,
        val type: com.kwanzatukule.features.authentication.domain.error.Error,
    ) : SignInEvent
}