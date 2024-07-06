package co.ke.xently.features.reviews.data.domain

data class ReviewFilters(
    val hasComments: Boolean = true,
    val starRating: Int? = null,
)