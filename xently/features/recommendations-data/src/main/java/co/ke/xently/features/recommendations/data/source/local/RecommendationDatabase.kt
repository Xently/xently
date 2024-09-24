package co.ke.xently.features.recommendations.data.source.local

import co.ke.xently.libraries.pagination.data.RemoteKeyDatabase

interface RecommendationDatabase : RemoteKeyDatabase {
    fun recommendationDao(): RecommendationDao
}