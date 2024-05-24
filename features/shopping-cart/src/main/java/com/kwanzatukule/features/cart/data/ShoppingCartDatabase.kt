package com.kwanzatukule.features.cart.data

import com.kwanzatukule.libraries.core.data.TransactionFacadeDatabase

interface ShoppingCartDatabase : TransactionFacadeDatabase {
    fun shoppingCartItemDao(): ShoppingCartItemDao
}