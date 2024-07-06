package co.ke.xently.features.auth.data.source

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "user")
@Serializable
data class UserEntity(
    @PrimaryKey
    val id: String,
    val email: String? = null,
    val emailVerified: Boolean = false,
    val name: String? = null,
    val profilePicUrl: String? = null,
    val refreshToken: String? = null,
    val accessToken: String? = null,
)
