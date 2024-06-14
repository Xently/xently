package co.ke.xently.features.access.control.data.local

import co.ke.xently.libraries.data.local.TransactionFacadeDatabase

interface AccessControlDatabase : TransactionFacadeDatabase {
    fun accessControlDao(): AccessControlDao
}