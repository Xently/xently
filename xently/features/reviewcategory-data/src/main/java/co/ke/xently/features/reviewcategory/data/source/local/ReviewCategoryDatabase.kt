package co.ke.xently.features.reviewcategory.data.source.local

import co.ke.xently.libraries.pagination.data.RemoteKeyDatabase

interface ReviewCategoryDatabase : RemoteKeyDatabase {
    fun reviewCategoryDao(): ReviewCategoryDao
}