package co.ke.xently.libraries.data.local

interface TransactionFacadeDatabase {
    suspend fun <R> withTransactionFacade(block: suspend () -> R): R
}