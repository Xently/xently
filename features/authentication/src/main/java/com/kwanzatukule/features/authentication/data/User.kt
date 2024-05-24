package com.kwanzatukule.features.authentication.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey
    val id: Int,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val accessToken: String?,
)
