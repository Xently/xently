package co.ke.xently.features.products.data.domain.error

import co.ke.xently.features.products.data.R
import co.ke.xently.libraries.data.core.UiText

enum class NameError : LocalFieldError {
    MISSING;

    override suspend fun toUiText(): UiText {
        return when (this) {
            MISSING -> UiText.StringResource(R.string.error_name_missing)
        }
    }
}