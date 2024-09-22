package co.ke.xently.features.products.data.domain.error

import co.ke.xently.libraries.data.core.UiText

data class UnclassifiedFieldError(val message: String) : LocalFieldError {
    override suspend fun toUiText(): UiText {
        return UiText.DynamicString(message)
    }
}