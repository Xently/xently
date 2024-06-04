package co.ke.xently.libraries.ui.core.domain

import java.text.DecimalFormat

fun Number.formatNumber(includeDecimal: Boolean = true): String {
    val pattern = if (includeDecimal) "#,###.##" else "#,###"
    return DecimalFormat(pattern).format(this)
}

fun Number.formatPrice(currency: String? = null, includeDecimal: Boolean = true): String {
    return if (currency.isNullOrBlank()) {
        formatNumber(includeDecimal = includeDecimal)
    } else {
        "$currency. ${formatNumber(includeDecimal = includeDecimal)}"
    }
}