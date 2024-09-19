package co.ke.xently.features.access.control.domain

import co.ke.xently.libraries.data.core.Link
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

private fun baseURLs(): Map<String, Link> {
    return mapOf(
        "product-categories" to Link(href = "/api/v1/categories/products"),
        "my-notifications" to Link(href = "/api/v1/notifications"),
        "remove-fcm-device-id" to Link(href = "/api/v1/firebase-devices"),
        "shops-associated-with-my-account" to Link(href = "/api/v1/shops/associated-with-me"),
        "store-categories" to Link(href = "/api/v1/categories/stores"),
        "upsert-fcm-device-id" to Link(href = "/api/v1/firebase-devices"),
        "google-sign-in" to Link(href = "/api/v1/auth/google"),
        "email-password-sign-in" to Link(href = "/api/v1/auth/sign-in"),
        "email-password-sign-up" to Link(href = "/api/v1/auth/sign-up"),
        "request-password-reset" to Link(href = "/api/v1/auth/request-password-reset"),
        "stores" to Link(href = "/api/v1/stores"),
        "rankings-statistics" to Link(href = "/api/v1/statistics/rankings"),
        "my-ranking-statistics" to Link(href = "/api/v1/statistics/my-rankings"),
        "my-profile-statistics" to Link(href = "/api/v1/statistics/my-profile"),
        "recommendations" to Link(href = "/api/v1/store/recommendations"),
    )
}

@Serializable
data class AccessControl(
    @SerialName("_links")
    val links: Map<String, Link> = baseURLs(),
) {
    val canAddShop: Boolean
        get() = links.containsKey("add-shop")

    internal fun copyWithDefaultMissingKeys(): AccessControl {
        return copy(links = buildMap { putAll(baseURLs()); putAll(links) })
    }

    val addShopUrl: String
        get() = this["add-shop"].hrefWithoutQueryParamTemplates()
    val googleSignInUrl: String
        get() = this["google-sign-in"].hrefWithoutQueryParamTemplates()
    val emailPasswordSignInUrl: String
        get() = this["email-password-sign-in"].hrefWithoutQueryParamTemplates()
    val emailPasswordSignUpUrl: String
        get() = this["email-password-sign-up"].hrefWithoutQueryParamTemplates()
    val requestPasswordResetUrl: String
        get() = this["request-password-reset"].hrefWithoutQueryParamTemplates()
    val storeCategoriesUrl: String
        get() = this["store-categories"].hrefWithoutQueryParamTemplates()
    val productCategoriesUrl: String
        get() = this["product-categories"].hrefWithoutQueryParamTemplates()
    val myNotificationsUrl: String
        get() = this["my-notifications"].hrefWithoutQueryParamTemplates()
    val upsertFcmDeviceIdUrl: String
        get() = this["upsert-fcm-device-id"].hrefWithoutQueryParamTemplates()
    val removeFcmDeviceIdUrl: String
        get() = this["remove-fcm-device-id"].hrefWithoutQueryParamTemplates()
    val shopsAssociatedWithMyAccountUrl: String
        get() = this["shops-associated-with-my-account"].hrefWithoutQueryParamTemplates()
    val storesUrl: String
        get() = this["stores"].hrefWithoutQueryParamTemplates()
    val rankingsStatisticsUrl: String
        get() = this["rankings-statistics"].hrefWithoutQueryParamTemplates()
    val myRankingStatisticsUrl: String
        get() = this["my-ranking-statistics"].hrefWithoutQueryParamTemplates()
    val myProfileStatisticsUrl: String
        get() = this["my-profile-statistics"].hrefWithoutQueryParamTemplates()
    val recommendationsUrl: String
        get() = this["recommendations"].hrefWithoutQueryParamTemplates()

    private operator fun get(ref: String): Link {
        return getLinkByRef(ref)
    }

    private fun getLinkByRef(ref: String): Link {
        return links[ref] ?: baseURLs()[ref]!!
    }
}