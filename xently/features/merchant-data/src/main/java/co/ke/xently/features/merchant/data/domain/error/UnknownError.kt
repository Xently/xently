package co.ke.xently.features.merchant.data.domain.error

import co.ke.xently.features.merchant.data.R
import co.ke.xently.libraries.data.core.RetryableError
import co.ke.xently.libraries.data.core.UiText

data object UnknownError : Error, RetryableError {
    override suspend fun toUiText(): UiText {
        return UiText.StringResource(R.string.error_message_default)
    }
}