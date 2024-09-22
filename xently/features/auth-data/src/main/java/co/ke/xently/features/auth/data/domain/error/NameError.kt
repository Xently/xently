package co.ke.xently.features.auth.data.domain.error

import co.ke.xently.features.auth.data.R
import co.ke.xently.libraries.data.core.UiText

enum class NameError : LocalFieldError {
    MISSING,
    MISSING_LAST_NAME;

    override suspend fun toUiText(): UiText {
        return when (this) {
            MISSING -> UiText.StringResource(R.string.error_name_missing)
            MISSING_LAST_NAME -> UiText.StringResource(R.string.error_last_name_missing)
        }
    }
}