package co.ke.xently.libraries.pagination.data

import co.ke.xently.libraries.data.local.TransactionFacadeDatabase

interface RemoteKeyDatabase : TransactionFacadeDatabase {
    fun remoteKeyDao(): RemoteKeyDao
}