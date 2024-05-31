package com.kwanzatukule.features.customer.complaints.data

interface CustomerComplaintDatabase : co.ke.xently.libraries.data.local.TransactionFacadeDatabase {
    fun customerComplaintDao(): CustomerComplaintDao
}