package com.kwanzatukule.features.core.domain

import java.text.DecimalFormat

fun Int.formatNumber(): String {
    return DecimalFormat("#,###.##").format(this)
}

fun Int.formatPrice(currency: String? = null): String {
    return if (currency.isNullOrBlank()) {
        formatNumber()
    } else {
        "$currency. ${formatNumber()}"
    }
}