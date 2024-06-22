package co.ke.xently.libraries.data.local

import androidx.room.TypeConverter
import kotlinx.datetime.Instant

object InstantConverter {
    @TypeConverter
    fun instantToLong(instant: Instant): Long {
        return instant.toEpochMilliseconds()
    }

    @TypeConverter
    fun longToInstant(instant: Long): Instant {
        return Instant.fromEpochMilliseconds(instant)
    }
}