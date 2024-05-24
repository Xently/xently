package com.kwanzatukule.libraries.data.customer.data

import com.kwanzatukule.libraries.core.data.TransactionFacadeDatabase

interface CustomerDatabase : TransactionFacadeDatabase {
    fun customerDao(): CustomerDao
}