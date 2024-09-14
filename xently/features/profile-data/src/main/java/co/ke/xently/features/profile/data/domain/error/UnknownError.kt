package co.ke.xently.features.profile.data.domain.error

import co.ke.xently.libraries.data.core.RetryableError

data object UnknownError : Error, RetryableError