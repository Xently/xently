package co.ke.xently.features.productcategory.data.source.local

import co.ke.xently.libraries.data.local.TransactionFacadeDatabase

interface ProductCategoryDatabase : TransactionFacadeDatabase {
    fun productCategoryDao(): ProductCategoryDao
}