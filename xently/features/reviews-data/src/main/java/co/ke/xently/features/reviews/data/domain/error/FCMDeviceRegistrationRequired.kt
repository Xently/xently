package co.ke.xently.features.reviews.data.domain.error

import co.ke.xently.features.reviews.data.R
import co.ke.xently.libraries.data.core.UiText

data object FCMDeviceRegistrationRequired : Error {
    override suspend fun toUiText(): UiText {
        return UiText.StringResource(R.string.error_message_fcm_device_registration_required)
    }
}