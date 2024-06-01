package co.ke.xently.features.auth.data.source

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
