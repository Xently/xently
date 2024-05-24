package com.kwanzatukule.features.authentication.presentation.signin

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.decompose.value.updateAndGet
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.kwanzatukule.features.authentication.data.UserRepository
import com.kwanzatukule.features.authentication.domain.error.Result
import com.kwanzatukule.features.authentication.presentation.asUiText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch


class SignInComponentImpl(
    context: ComponentContext,
    component: SignInComponent,
    private val repository: UserRepository,
) : SignInComponent by component, ComponentContext by context {
    private val componentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val _uiState = MutableValue(SignInUIState())
    override val uiState: Value<SignInUIState> = _uiState

    private val _event = Channel<SignInEvent>()
    override val event: Flow<SignInEvent> = _event.receiveAsFlow()

    init {
        lifecycle.doOnDestroy(componentScope::cancel)
    }

    override fun setEmail(email: String) {
        _uiState.update {
            it.copy(email = email)
        }
    }

    override fun setPassword(password: String) {
        _uiState.update {
            it.copy(password = password)
        }
    }

    override fun togglePasswordVisibility() {
        _uiState.update {
            it.copy(isPasswordVisible = !it.isPasswordVisible)
        }
    }

    override fun signIn() {
        componentScope.launch {
            val state = _uiState.updateAndGet {
                it.copy(isLoading = true)
            }
            when (val result = repository.signIn(email = state.email, password = state.password)) {
                is Result.Failure -> {
                    _event.send(SignInEvent.Error(result.error.asUiText(), result.error))
                }

                is Result.Success -> {
                    // Sign-in can be requested from any screen after an access token is
                    // flagged as expired by the server. Therefore, instead of navigating
                    // to a dedicated screen, we should simply retain the screen the
                    // authentication was requested from, hence creating a good UX.
                    handleBackPress()
                }
            }
        }.invokeOnCompletion {
            _uiState.update {
                it.copy(isLoading = false)
            }
        }
    }
}