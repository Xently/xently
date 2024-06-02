package co.ke.xently.features.reviews.data.source.local

import co.ke.xently.libraries.data.local.TransactionFacadeDatabase

interface ReviewDatabase : TransactionFacadeDatabase {
    fun reviewDao(): ReviewDao
}
