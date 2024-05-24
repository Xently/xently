package com.kwanzatukule.features.core.data

fun interface AccessTokenProvider {
    suspend fun getAccessToken(): String?
}