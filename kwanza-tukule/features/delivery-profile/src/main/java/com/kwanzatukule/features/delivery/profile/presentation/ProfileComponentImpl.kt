package com.kwanzatukule.features.delivery.profile.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

class ProfileComponentImpl(
    context: ComponentContext,
    component: ProfileComponent,
) : ProfileComponent by component, ComponentContext by context {
    private val componentScope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        lifecycle.doOnDestroy(componentScope::cancel)
    }
}