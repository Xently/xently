package com.kwanzatukule.features.catalogue.data

import com.kwanzatukule.features.catalogue.domain.Product

fun interface ShoppingCartChecker {
    suspend fun containsProduct(product: Product): Boolean
}