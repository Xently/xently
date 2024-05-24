package com.kwanzatukule.features.authentication.presentation.resetpassword

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.kwanzatukule.features.authentication.data.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

class DefaultResetPasswordComponent(
    context: ComponentContext,
    private val repository: UserRepository,
    private val component: ResetPasswordComponent,
) : ResetPasswordComponent by component, ComponentContext by context {
    private val componentScope: CoroutineScope = CoroutineScope(Dispatchers.Main)

    init {
        lifecycle.doOnDestroy(componentScope::cancel)
    }
}