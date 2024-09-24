package co.ke.xently.features.products.data.domain.error

import co.ke.xently.features.products.data.R
import co.ke.xently.libraries.data.core.UiText

enum class PriceError : LocalFieldError {
    INVALID,
    ZERO_OR_LESS;

    override suspend fun toUiText(): UiText {
        return when (this) {
            INVALID -> UiText.StringResource(R.string.error_price_invalid)
            ZERO_OR_LESS -> UiText.StringResource(R.string.error_price_zero_or_less)
        }
    }
}