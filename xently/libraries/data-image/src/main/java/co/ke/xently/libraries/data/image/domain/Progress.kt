package co.ke.xently.libraries.data.image.domain

data class Progress(
    val bytesSentTotal: Long = 0,
    val contentLength: Long = 0,
) : File {
    @Suppress("unused")
    val isIndeterminate: Boolean
        get() = bytesSentTotal <= 0 || contentLength <= 0

    fun calculate(): Float {
        return (bytesSentTotal / contentLength).toFloat() * 1
    }
}