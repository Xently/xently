package co.ke.xently.features.reviews.data.domain

import co.ke.xently.features.reviewcategory.data.domain.ReviewCategory
import co.ke.xently.libraries.pagination.data.PagedResponse


data class SummaryAndCategories(
    val shop: Rating,
    val store: Rating,
    val storeReviewCategories: PagedResponse<ReviewCategory>,
)