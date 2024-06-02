package co.ke.xently.features.customers.data.source.local

import co.ke.xently.libraries.data.local.TransactionFacadeDatabase

interface CustomerDatabase : TransactionFacadeDatabase {
    fun customerDao(): CustomerDao
}