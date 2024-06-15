package co.ke.xently.features.auth.data.domain.error

enum class GoogleAuthenticationError : Error {
    INVALID_GOOGLE_ID_RESPONSE,
    UNRECOGNISED_CREDENTIAL_TYPE,
    CANCELLED,
    INTERRUPTED,
    UNSUPPORTED_PROVIDER,
    NO_CREDENTIALS_FOUND,
    UNEXPECTED_ERROR,
}