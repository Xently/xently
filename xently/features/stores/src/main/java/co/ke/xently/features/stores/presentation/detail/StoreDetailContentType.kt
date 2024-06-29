package co.ke.xently.features.stores.presentation.detail

import androidx.annotation.StringRes
import co.ke.xently.features.stores.R

internal enum class StoreDetailContentType(@StringRes val title: Int) {
    AllStoreProducts(R.string.tab_all_store_products),
    RecommendedProducts(R.string.tab_recommended_products),
}