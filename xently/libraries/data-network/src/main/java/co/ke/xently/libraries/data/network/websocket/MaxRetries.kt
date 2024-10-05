package co.ke.xently.libraries.data.network.websocket

sealed interface MaxRetries {
    data object Infinite : MaxRetries
    data class Finite(val retries: Int) : MaxRetries {
        init {
            require(retries > 0) { "Max retries must be greater than 0" }
        }
    }
}