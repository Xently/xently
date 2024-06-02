package co.ke.xently.features.reviews.data.source.local

import co.ke.xently.libraries.data.local.TransactionFacadeDatabase

interface ReviewCategoryDatabase : TransactionFacadeDatabase {
    fun reviewCategoryDao(): ReviewCategoryDao
}