package co.ke.xently.features.stores.data.domain.error

import co.ke.xently.features.stores.data.R
import co.ke.xently.libraries.data.core.UiText

enum class PhoneError : LocalFieldError {
    INVALID_FORMAT;

    override suspend fun toUiText(): UiText {
        return when(this){
            INVALID_FORMAT -> UiText.StringResource(R.string.error_phone_invalid_format)
        }
    }
}