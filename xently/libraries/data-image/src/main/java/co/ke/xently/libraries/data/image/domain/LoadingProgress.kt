package co.ke.xently.libraries.data.image.domain

data class LoadingProgress(
    val bytesSentTotal: Long = 0,
    val contentLength: Long = 0,
    override val id: String? = null,
) : Image {
    @Suppress("unused")
    val isIndeterminate: Boolean
        get() = bytesSentTotal <= 0 || contentLength <= 0

    fun calculate(): Float {
        return (bytesSentTotal / contentLength).toFloat() * 1
    }

    override fun key(vararg args: Any): Any {
        return buildString {
            append(bytesSentTotal + contentLength)
            append(*args)
        }
    }
}