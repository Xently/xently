package co.ke.xently.features.storeservice.data.source.local

import co.ke.xently.libraries.data.local.TransactionFacadeDatabase

interface StoreServiceDatabase : TransactionFacadeDatabase {
    fun storeServiceDao(): StoreServiceDao
}