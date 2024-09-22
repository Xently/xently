package co.ke.xently.features.merchant.data.domain.error

import co.ke.xently.features.merchant.data.R
import co.ke.xently.libraries.data.core.UiText

enum class EmailError : FieldError {
    INVALID_FORMAT;

    override suspend fun toUiText(): UiText {
        return when (this) {
            INVALID_FORMAT -> UiText.StringResource(R.string.error_email_invalid_format)
        }
    }
}