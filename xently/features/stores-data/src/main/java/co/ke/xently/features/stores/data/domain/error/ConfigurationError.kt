package co.ke.xently.features.stores.data.domain.error

enum class ConfigurationError : Error {
    ShopSelectionRequired,
    StoreSelectionRequired,
    FCMDeviceRegistrationRequired,
}