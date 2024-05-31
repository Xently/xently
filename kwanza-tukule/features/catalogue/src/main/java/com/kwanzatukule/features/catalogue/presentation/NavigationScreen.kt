package com.kwanzatukule.features.catalogue.presentation

import com.kwanzatukule.features.catalogue.domain.Category
import com.kwanzatukule.features.catalogue.domain.Product
import kotlinx.serialization.Serializable

@Serializable
sealed interface NavigationScreen {
    @Serializable
    data class Catalogue(val category: Category?) : NavigationScreen

    @Serializable
    data class ProductDetail(val product: Product) : NavigationScreen
}