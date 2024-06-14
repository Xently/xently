package co.ke.xently.features.shops.presentation.utils

import co.ke.xently.features.shops.R
import co.ke.xently.features.shops.data.domain.error.DataError
import co.ke.xently.features.shops.data.domain.error.EmailError
import co.ke.xently.features.shops.data.domain.error.Error
import co.ke.xently.features.shops.data.domain.error.NameError
import co.ke.xently.features.shops.data.domain.error.WebsiteError

fun Error.asUiText(): UiText {
    return when (this) {
        DataError.Network.REQUEST_TIMEOUT -> UiText.StringResource(
            R.string.the_request_timed_out
        )

        DataError.Network.TOO_MANY_REQUESTS -> UiText.StringResource(
            R.string.youve_hit_your_rate_limit
        )

        DataError.Network.NO_INTERNET -> UiText.StringResource(
            R.string.no_internet
        )

        DataError.Network.PAYLOAD_TOO_LARGE -> UiText.StringResource(
            R.string.file_too_large
        )

        DataError.Network.SERVER_ERROR -> UiText.StringResource(
            R.string.server_error
        )

        DataError.Network.SERIALIZATION -> UiText.StringResource(
            R.string.error_serialization
        )

        DataError.Network.UNKNOWN -> UiText.StringResource(
            R.string.unknown_error
        )

        DataError.Local.DISK_FULL -> UiText.StringResource(
            R.string.error_disk_full
        )

        EmailError.INVALID_FORMAT -> UiText.StringResource(R.string.error_email_invalid_format)
        NameError.MISSING -> UiText.StringResource(R.string.error_name_missing)
        WebsiteError.INVALID_FORMAT -> UiText.StringResource(R.string.error_website_invalid_format)
    }
}