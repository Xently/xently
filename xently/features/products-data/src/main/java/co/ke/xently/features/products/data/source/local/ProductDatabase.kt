package co.ke.xently.features.products.data.source.local

import co.ke.xently.libraries.data.local.TransactionFacadeDatabase

interface ProductDatabase : TransactionFacadeDatabase {
    fun productDao(): ProductDao
}