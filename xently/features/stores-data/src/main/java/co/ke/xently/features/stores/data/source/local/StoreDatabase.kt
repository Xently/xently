package co.ke.xently.features.stores.data.source.local

import co.ke.xently.libraries.data.local.TransactionFacadeDatabase

interface StoreDatabase : TransactionFacadeDatabase {
    fun storeDao(): StoreDao
}