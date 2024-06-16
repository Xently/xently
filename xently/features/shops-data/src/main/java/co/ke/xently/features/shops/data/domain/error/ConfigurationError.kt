package co.ke.xently.features.shops.data.domain.error

enum class ConfigurationError : Error {
    ShopSelectionRequired,
    FCMDeviceRegistrationRequired,
}