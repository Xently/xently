package co.ke.xently.features.profile.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import co.ke.xently.features.profile.data.domain.ProfileStatistic
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

@Entity(tableName = "profile_statistics")
data class ProfileStatisticEntity(
    val statistic: ProfileStatistic,
    @PrimaryKey
    val id: Int = 1,
    val lastUpdated: Instant = Clock.System.now(),
)