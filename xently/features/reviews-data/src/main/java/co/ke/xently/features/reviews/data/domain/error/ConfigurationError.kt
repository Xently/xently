package co.ke.xently.features.reviews.data.domain.error

import co.ke.xently.features.reviewcategory.data.R
import co.ke.xently.libraries.data.core.UiText

enum class ConfigurationError : Error {
    ShopSelectionRequired,
    StoreSelectionRequired;

    override suspend fun toUiText(): UiText {
        return when (this) {
            StoreSelectionRequired -> UiText.StringResource(R.string.error_store_not_selected)
            ShopSelectionRequired -> UiText.StringResource(R.string.error_shop_not_selected)
        }
    }
}