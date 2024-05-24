package com.kwanzatukule.libraries.core.data

interface TransactionFacadeDatabase {
    suspend fun <R> withTransactionFacade(block: suspend () -> R): R
}