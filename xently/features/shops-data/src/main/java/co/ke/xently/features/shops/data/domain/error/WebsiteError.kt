package co.ke.xently.features.shops.data.domain.error

import co.ke.xently.features.shops.data.R
import co.ke.xently.libraries.data.core.UiText

enum class WebsiteError : FieldError {
    INVALID_FORMAT;

    override suspend fun toUiText(): UiText {
        return when (this) {
            INVALID_FORMAT -> UiText.StringResource(R.string.error_website_invalid_format)
        }
    }
}