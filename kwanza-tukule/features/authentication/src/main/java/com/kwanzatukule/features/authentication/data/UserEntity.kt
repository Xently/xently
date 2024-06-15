package com.kwanzatukule.features.authentication.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "user")
@Serializable
data class UserEntity(
    @PrimaryKey
    val id: String,
    val firstName: String?,
    val lastName: String?,
    val email: String?,
    val accessToken: String?,
)
