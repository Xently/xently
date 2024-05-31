package com.kwanzatukule.features.authentication.data

import co.ke.xently.libraries.data.local.TransactionFacadeDatabase

interface AuthenticationDatabase : TransactionFacadeDatabase {
    fun userDao(): UserDao
}