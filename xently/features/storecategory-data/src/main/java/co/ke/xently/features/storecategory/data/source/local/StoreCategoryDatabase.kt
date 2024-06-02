package co.ke.xently.features.storecategory.data.source.local

import co.ke.xently.libraries.data.local.TransactionFacadeDatabase

interface StoreCategoryDatabase : TransactionFacadeDatabase {
    fun storeCategoryDao(): StoreCategoryDao
}