package co.ke.xently.features.products.data.source.local

import co.ke.xently.libraries.pagination.data.RemoteKeyDatabase

interface ProductDatabase : RemoteKeyDatabase {
    fun productDao(): ProductDao
}