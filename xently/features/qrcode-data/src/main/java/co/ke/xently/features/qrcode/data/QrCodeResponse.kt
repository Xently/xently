package co.ke.xently.features.qrcode.data

import co.ke.xently.libraries.data.core.Link
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class QrCodeResponse(
    val pointsEarned: Int,
    val storeVisitCount: Int,
    val storePoints: Int,
    val totalXentlyPoints: Int,
    @SerialName("_links")
    val links: Map<String, Link> = emptyMap(),
) {
    @Transient
    val reviewCategoriesUrl = links["store-review-categories-with-my-ratings"]
        ?.hrefWithoutQueryParamTemplates()
}