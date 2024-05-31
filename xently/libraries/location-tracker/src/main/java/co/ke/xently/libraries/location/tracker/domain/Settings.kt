package co.ke.xently.libraries.location.tracker.domain

import com.google.android.gms.location.Priority
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class Settings(
    val refreshInterval: Duration = 10.seconds,
    val accuracy: Accuracy = Accuracy.PRIORITY_HIGH_ACCURACY,
) {
    enum class Accuracy(val priority: Int) {
        PRIORITY_HIGH_ACCURACY(Priority.PRIORITY_HIGH_ACCURACY),
        PRIORITY_BALANCED_POWER_ACCURACY(Priority.PRIORITY_BALANCED_POWER_ACCURACY),
        PRIORITY_LOW_POWER(Priority.PRIORITY_LOW_POWER),
        PRIORITY_PASSIVE(Priority.PRIORITY_PASSIVE),
    }
}