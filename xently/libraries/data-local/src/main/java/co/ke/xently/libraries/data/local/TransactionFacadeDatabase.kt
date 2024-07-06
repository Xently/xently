package co.ke.xently.libraries.data.local

interface TransactionFacadeDatabase {
    fun serverResponseCacheDao(): ServerResponseCacheDao
    suspend fun <R> withTransactionFacade(block: suspend () -> R): R
}