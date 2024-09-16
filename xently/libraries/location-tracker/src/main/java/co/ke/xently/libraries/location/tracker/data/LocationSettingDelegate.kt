package co.ke.xently.libraries.location.tracker.data

import co.ke.xently.libraries.location.tracker.domain.Location
import com.russhwolf.settings.Settings
import kotlinx.datetime.Clock
import timber.log.Timber
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds

class LocationSettingDelegate(
    private val defaultValue: Location?,
    private val key: String = KEY,
) : ReadWriteProperty<Any?, Location?> {
    private val settings by lazy { Settings() }

    override fun getValue(thisRef: Any?, property: KProperty<*>): Location? {
        val lastUpdateTime = settings.getLongOrNull("${key}_last_update_time")
            ?: return null

        val periodSinceLastUpdate = Clock.System.now().toEpochMilliseconds() - lastUpdateTime
        val acceptableSettingValidityPeriod = 24.hours
        if (periodSinceLastUpdate.milliseconds > acceptableSettingValidityPeriod) {
            Timber.i("""Removing '$KEY' from settings. Period since last update has exceeded $acceptableSettingValidityPeriod""")

            settings.remove("${key}_lat")
            settings.remove("${key}_lon")
            settings.remove("${key}_last_update_time")

            Timber.i("""Successfully removed '$KEY' from settings.""")

            return null
        }

        val latitude = settings.getDoubleOrNull("${key}_lat") ?: defaultValue?.latitude
        val longitude = settings.getDoubleOrNull("${key}_lon") ?: defaultValue?.longitude

        return if (latitude == null || longitude == null) {
            Timber.w("""Unexpected error was encountered when retrieving '$KEY' from settings...""")
            null
        } else {
            Location(latitude, longitude, isCached = true)
        }
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Location?) {
        if (value == null) return
        Timber.i("""Saving '$value' to '$KEY'...""")
        settings.putDouble("${key}_lat", value.latitude)
        settings.putDouble("${key}_lon", value.longitude)
        settings.putLong("${key}_last_update_time", Clock.System.now().toEpochMilliseconds())
        Timber.i("""Successfully saved '$value' to '$KEY'""")
    }

    private companion object {
        private const val KEY = "current_location"
    }
}