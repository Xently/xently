package co.ke.xently.features.shops.data.source.local

import co.ke.xently.libraries.data.local.TransactionFacadeDatabase

interface ShopDatabase : TransactionFacadeDatabase {
    fun shopDao(): ShopDao
}