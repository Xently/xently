package co.ke.xently.features.customers.data.source.local

import co.ke.xently.libraries.pagination.data.RemoteKeyDatabase

interface CustomerDatabase : RemoteKeyDatabase {
    fun customerDao(): CustomerDao
}