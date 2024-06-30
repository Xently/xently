package co.ke.xently.features.access.control.domain

import co.ke.xently.features.access.control.BuildConfig.BASE_URL
import co.ke.xently.libraries.data.core.Link
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccessControl(
    @SerialName("_links")
    val links: Map<String, Link> = BASE_URLS,
) {
    val canAddShop: Boolean
        get() = links.containsKey("add-shop")

    internal fun copyWithDefaultMissingKeys(): AccessControl {
        return copy(links = buildMap { putAll(BASE_URLS); putAll(links) })
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
    val visitRankingUrl: String
        get() = this["visit-ranking"].hrefWithoutQueryParamTemplates()
    val myVisitRankingUrl: String
        get() = this["my-visit-ranking"].hrefWithoutQueryParamTemplates()

    private operator fun get(ref: String): Link {
        return getLinkByRef(ref)
    }

    private fun getLinkByRef(ref: String): Link {
        return links[ref] ?: BASE_URLS[ref]!!
    }

    companion object {
        private val BASE_URLS = mapOf(
            "product-categories" to Link(href = "$BASE_URL/categories/products"),
            "my-notifications" to Link(href = "$BASE_URL/notifications"),
            "remove-fcm-device-id" to Link(href = "$BASE_URL/firebase-devices"),
            "shops-associated-with-my-account" to Link(href = "$BASE_URL/shops/associated-with-me"),
            "store-categories" to Link(href = "$BASE_URL/categories/stores"),
            "upsert-fcm-device-id" to Link(href = "$BASE_URL/firebase-devices"),
            "google-sign-in" to Link(href = "$BASE_URL/auth/google"),
            "email-password-sign-in" to Link(href = "$BASE_URL/auth/sign-in"),
            "email-password-sign-up" to Link(href = "$BASE_URL/auth/sign-up"),
            "request-password-reset" to Link(href = "$BASE_URL/auth/request-password-reset"),
            "stores" to Link(href = "${BASE_URL}/stores"),
            "visit-ranking" to Link(href = "$BASE_URL/statistics/rankings"),
            "my-visit-ranking" to Link(href = "$BASE_URL/statistics/my-rankings"),
        )
    }
}