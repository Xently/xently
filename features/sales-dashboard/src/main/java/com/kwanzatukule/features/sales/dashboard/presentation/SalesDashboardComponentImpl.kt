package com.kwanzatukule.features.sales.dashboard.presentation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.update
import com.arkivanov.essenty.lifecycle.doOnDestroy
import com.kwanzatukule.features.sales.dashboard.data.SalesDashboardRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SalesDashboardComponentImpl(
    context: ComponentContext,
    component: SalesDashboardComponent,
    private val repository: SalesDashboardRepository,
) : SalesDashboardComponent by component, ComponentContext by context {
    private val componentScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _uiState = MutableValue(SalesDashboardUiState())
    override val uiState: Value<SalesDashboardUiState> = _uiState

    init {
        lifecycle.doOnDestroy(componentScope::cancel)
        componentScope.launch {
            repository.getSalesDashboardContent().collect { content ->
                _uiState.update {
                    it.copy(content = content)
                }
            }
        }
    }
}