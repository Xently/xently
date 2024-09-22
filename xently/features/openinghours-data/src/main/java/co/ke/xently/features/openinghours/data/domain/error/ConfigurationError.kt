package co.ke.xently.features.openinghours.data.domain.error

import co.ke.xently.features.openinghours.data.R
import co.ke.xently.libraries.data.core.UiText

enum class ConfigurationError : Error {
    FCMDeviceRegistrationRequired;

    override suspend fun toUiText(): UiText {
        return when (this) {
            FCMDeviceRegistrationRequired -> UiText.StringResource(R.string.error_message_fcm_device_registration_required)
        }
    }
}