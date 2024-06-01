package co.ke.xently.features.auth.data.source

import co.ke.xently.libraries.data.local.TransactionFacadeDatabase

interface AuthenticationDatabase : TransactionFacadeDatabase {
    fun userDao(): UserDao
}