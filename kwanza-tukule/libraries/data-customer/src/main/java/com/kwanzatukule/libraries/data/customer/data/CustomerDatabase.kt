package com.kwanzatukule.libraries.data.customer.data

import co.ke.xently.libraries.data.local.TransactionFacadeDatabase

interface CustomerDatabase : TransactionFacadeDatabase {
    fun customerDao(): CustomerDao
}