package co.ke.xently.features.auth.data.domain.error

import co.ke.xently.features.auth.data.R
import co.ke.xently.libraries.data.core.UiText

typealias FieldName = String

data class RemoteFieldError(val errors: Map<FieldName, List<LocalFieldError>>) : FieldError {
    override suspend fun toUiText(): UiText {
        return UiText.StringResource(R.string.error_message_bad_request)
    }
}