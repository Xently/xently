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

fun Number.coolFormat(iteration: Int = 0): String {
    if (this.toDouble() < 1000) return toString()
        .replace(".0", "")
        .reversed()
        .chunked(3)
        .joinToString(",")
        .reversed()
    val d = (toLong() / 100) / 10.0
    val isRound =
        (d * 10) % 10 == 0.0 //true if the decimal part is equal to 0 (then it's trimmed anyway)
    //this determines the class, i.e. 'k', 'm' etc
    //this decides whether to trim the decimals
    // (int) d * 10 / 10 drops the decimal
    return if (d < 1000) //this determines the class, i.e. 'k', 'm' etc
        (if (isRound || d > 9.99) //this decides whether to trim the decimals
            d.toInt() * 10 / 10 else d.toString() + "" // (int) d * 10 / 10 drops the decimal
                ).toString() + "" + charArrayOf('k', 'm', 'b', 't')[iteration]
    else d.coolFormat(iteration + 1)
}