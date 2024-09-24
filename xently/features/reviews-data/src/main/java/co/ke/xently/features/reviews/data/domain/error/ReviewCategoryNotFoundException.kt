package co.ke.xently.features.reviews.data.domain.error

import co.ke.xently.features.reviews.data.R
import co.ke.xently.libraries.data.core.UiText

data object ReviewCategoryNotFound : Error {
    override suspend fun toUiText(): UiText {
        return UiText.StringResource(R.string.error_message_review_category_not_found)
    }
}

class ReviewCategoryNotFoundException : RuntimeException()