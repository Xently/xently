package co.ke.xently.features.products.data.domain.error

import co.ke.xently.features.products.data.R
import co.ke.xently.libraries.data.core.UiText

sealed interface DescriptionError : LocalFieldError {
    data class TooLong(val acceptableLength: Int) : DescriptionError

    override suspend fun toUiText(): UiText {
        return when (this) {
            is TooLong -> UiText.StringResource(
                R.string.error_description_too_long,
                arrayOf(acceptableLength),
            )
        }
    }
}