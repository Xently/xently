package co.ke.xently.features.auth.data.domain.error

import co.ke.xently.features.auth.data.R
import co.ke.xently.libraries.data.core.UiText

data object DuplicateUserAccountError : DataError {
    override suspend fun toUiText(): UiText {
        return UiText.StringResource(R.string.error_duplicate_user_account)
    }
}