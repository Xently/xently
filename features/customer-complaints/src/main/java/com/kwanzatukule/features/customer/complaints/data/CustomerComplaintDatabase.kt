package com.kwanzatukule.features.customer.complaints.data

import com.kwanzatukule.libraries.core.data.TransactionFacadeDatabase

interface CustomerComplaintDatabase : TransactionFacadeDatabase {
    fun customerComplaintDao(): CustomerComplaintDao
}