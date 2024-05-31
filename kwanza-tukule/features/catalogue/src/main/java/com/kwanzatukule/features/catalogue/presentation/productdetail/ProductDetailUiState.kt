package com.kwanzatukule.features.catalogue.presentation.productdetail

import androidx.compose.runtime.Stable
import com.kwanzatukule.features.catalogue.domain.Product

@Stable
data class ProductDetailUiState(
    val product: Product,
    val isLoading: Boolean = false,
)