package co.ke.xently.features.reviews.domain

data class Star(
    val number: Int,
    val selected: Boolean,
) {
    override fun toString(): String {
        return number.toString()
    }
}