package co.ke.xently.features.reviews.data.source.local

import co.ke.xently.libraries.pagination.data.RemoteKeyDatabase

interface ReviewDatabase : RemoteKeyDatabase {
    fun reviewDao(): ReviewDao
    fun reviewRequestDao(): ReviewRequestDao
}
