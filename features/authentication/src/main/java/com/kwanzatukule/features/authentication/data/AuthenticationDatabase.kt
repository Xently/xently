package com.kwanzatukule.features.authentication.data

import com.kwanzatukule.libraries.core.data.TransactionFacadeDatabase

interface AuthenticationDatabase : TransactionFacadeDatabase {
    fun userDao(): UserDao
}