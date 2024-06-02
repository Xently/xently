package co.ke.xently.features.reviewcategory.data.source.local

import co.ke.xently.libraries.data.local.TransactionFacadeDatabase

interface ReviewCategoryDatabase : TransactionFacadeDatabase {
    fun reviewCategoryDao(): ReviewCategoryDao
}