package co.ke.xently.features.customers.data.domain.error

import co.ke.xently.libraries.data.core.RetryableError

data object UnknownError : Error, RetryableError