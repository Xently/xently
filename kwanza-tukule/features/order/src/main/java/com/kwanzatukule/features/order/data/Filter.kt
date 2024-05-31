package com.kwanzatukule.features.order.data

import com.kwanzatukule.features.order.domain.Order

data class Filter(val query: String? = null, val status: Order.Status? = null)