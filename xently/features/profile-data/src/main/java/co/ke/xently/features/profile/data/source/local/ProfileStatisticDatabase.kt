package co.ke.xently.features.profile.data.source.local

import co.ke.xently.libraries.data.local.TransactionFacadeDatabase

interface ProfileStatisticDatabase : TransactionFacadeDatabase {
    fun profileStatisticDao(): ProfileStatisticDao
}