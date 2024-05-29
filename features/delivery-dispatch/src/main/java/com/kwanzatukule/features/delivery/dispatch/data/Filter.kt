package com.kwanzatukule.features.delivery.dispatch.data

import com.kwanzatukule.features.delivery.dispatch.domain.Dispatch

data class Filter(val query: String? = null, val status: Dispatch.Status? = null)