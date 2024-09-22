package co.ke.xently.features.profile.data.domain.error

import co.ke.xently.features.profile.data.R
import co.ke.xently.libraries.data.core.UiText

enum class EmailError : LocalFieldError {
    INVALID_FORMAT;

    override suspend fun toUiText(): UiText {
        return when (this) {
            INVALID_FORMAT -> UiText.StringResource(R.string.error_email_invalid_format)
        }
    }
}