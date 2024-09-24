package co.ke.xently.features.shops.data.domain.error

import co.ke.xently.features.shops.data.R
import co.ke.xently.libraries.data.core.UiText

enum class ConfigurationError : Error {
    ShopSelectionRequired;

    override suspend fun toUiText(): UiText {
        return when (this) {
            ShopSelectionRequired -> UiText.StringResource(R.string.error_shop_not_selected)
        }
    }
}