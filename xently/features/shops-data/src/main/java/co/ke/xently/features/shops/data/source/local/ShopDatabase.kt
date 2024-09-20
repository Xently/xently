package co.ke.xently.features.shops.data.source.local

import co.ke.xently.libraries.pagination.data.RemoteKeyDatabase

interface ShopDatabase : RemoteKeyDatabase {
    fun shopDao(): ShopDao
    suspend fun postActivateShop() {}
}