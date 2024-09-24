package co.ke.xently.features.stores.data.source.local

import co.ke.xently.libraries.pagination.data.RemoteKeyDatabase

interface StoreDatabase : RemoteKeyDatabase {
    fun storeDao(): StoreDao
}