package co.ke.xently.features.storeservice.data.domain.error

import co.ke.xently.features.storeservice.data.R
import co.ke.xently.libraries.data.core.UiText

sealed interface DataError : Error {
    enum class Local : DataError {
        DISK_FULL;

        override suspend fun toUiText(): UiText {
            return when (this) {
                DISK_FULL -> UiText.StringResource(R.string.error_disk_full)
            }
        }
    }
}