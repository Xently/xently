package co.ke.xently.features.notification.topic.data.domain.error

import co.ke.xently.libraries.data.core.RetryableError

data object UnknownError : Error, RetryableError