package co.ke.xently.libraries.data.network.websocket.utils

import kotlin.math.pow
import kotlin.math.roundToLong
import kotlin.time.Duration

internal interface NextRetryDelayMilliseconds {
    operator fun invoke(attempt: Int, initialRetryDelay: Duration, attemptRestart: Int = 1): Long

    companion object ExponentialBackoff : NextRetryDelayMilliseconds {
        override operator fun invoke(
            attempt: Int,
            initialRetryDelay: Duration,
            attemptRestart: Int,
        ): Long {
            assert(attemptRestart > 0) { "`attemptRestart` must be greater than 0" }
            return 2f.pow(attempt % attemptRestart)
                .roundToLong() * initialRetryDelay.inWholeMilliseconds
        }
    }
}