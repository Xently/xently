package co.ke.xently.features.recommendations.data.source.local

import co.ke.xently.libraries.data.local.TransactionFacadeDatabase

interface RecommendationDatabase : TransactionFacadeDatabase {
    fun recommendationDao(): RecommendationDao
}