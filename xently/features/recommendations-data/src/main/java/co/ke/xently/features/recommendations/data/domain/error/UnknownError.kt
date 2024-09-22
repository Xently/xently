package co.ke.xently.features.recommendations.data.domain.error

import co.ke.xently.features.recommendations.data.R
import co.ke.xently.libraries.data.core.RetryableError
import co.ke.xently.libraries.data.core.UiText

data object UnknownError : Error, RetryableError {
    override suspend fun toUiText(): UiText {
        return UiText.StringResource(R.string.error_message_default)
    }
}