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

    fun copyWithDefaultMissingKeys(): AccessControl {
        return copy(links = buildMap { putAll(BASE_URLS); putAll(links) })
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
        )
    }
}