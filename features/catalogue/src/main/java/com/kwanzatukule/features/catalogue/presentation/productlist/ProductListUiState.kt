package com.kwanzatukule.features.catalogue.presentation.productlist

import androidx.compose.runtime.Stable
import com.kwanzatukule.features.catalogue.domain.Category

@Stable
data class ProductListUiState(
    val query: String? = null,
    val category: Category? = null,
    val isLoading: Boolean = false,
)