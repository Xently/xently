package com.kwanzatukule.features.cart.data

interface ShoppingCartDatabase : co.ke.xently.libraries.data.local.TransactionFacadeDatabase {
    fun shoppingCartItemDao(): ShoppingCartItemDao
}