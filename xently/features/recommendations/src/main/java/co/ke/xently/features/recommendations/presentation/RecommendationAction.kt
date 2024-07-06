package co.ke.xently.features.recommendations.presentation

import co.ke.xently.features.productcategory.data.domain.ProductCategory
import co.ke.xently.features.storecategory.data.domain.StoreCategory
import co.ke.xently.libraries.location.tracker.domain.Location

internal sealed interface RecommendationAction {
    class ChangeLocationQuery(val query: String) : RecommendationAction
    class SearchLocation(val query: String) : RecommendationAction
    class ChangeLocation(val location: Location) : RecommendationAction
    class ChangeMinimumPrice(val price: String) : RecommendationAction
    class ChangeMaximumPrice(val price: String) : RecommendationAction
    class StoreSelectCategory(val category: StoreCategory) : RecommendationAction
    class StoreRemoveCategory(val category: StoreCategory) : RecommendationAction
    class ProductSelectCategory(val category: ProductCategory) : RecommendationAction
    class ProductRemoveCategory(val category: ProductCategory) : RecommendationAction
}