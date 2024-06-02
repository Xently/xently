package co.ke.xently.features.merchant.data.source.local

import co.ke.xently.libraries.data.local.TransactionFacadeDatabase

interface MerchantDatabase : TransactionFacadeDatabase {
    fun merchantDao(): MerchantDao
}