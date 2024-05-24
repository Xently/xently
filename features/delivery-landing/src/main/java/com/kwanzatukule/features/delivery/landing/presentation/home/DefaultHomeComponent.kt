package com.kwanzatukule.features.delivery.landing.presentation.home

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.doOnDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel

class DefaultHomeComponent(
    context: ComponentContext,
    component: HomeComponent,
) : HomeComponent by component, ComponentContext by context {
    private val componentScope: CoroutineScope = CoroutineScope(Dispatchers.Main)

    init {
        lifecycle.doOnDestroy(componentScope::cancel)
    }
}