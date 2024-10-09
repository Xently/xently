package co.ke.xently.libraries.data.network.websocket.utils

import kotlin.math.pow
import kotlin.math.roundToLong
import kotlin.time.Duration

internal fun interface NextRetryDelayMilliseconds {
    operator fun invoke(attempt: Int, initialRetryDelay: Duration): Long

    companion object ExponentialBackoff : NextRetryDelayMilliseconds {
        override operator fun invoke(attempt: Int, initialRetryDelay: Duration): Long {
            return 2f.pow(attempt).roundToLong() * initialRetryDelay.inWholeMilliseconds
        }
    }
}