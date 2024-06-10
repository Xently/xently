package co.ke.xently.libraries.data.image.domain


sealed interface Image {
    val id: String?
    fun key(vararg args: Any): Any

    sealed interface Error : Image {
        data class InvalidFileError(override val id: String? = null) : Error {
            override fun key(vararg args: Any): Any {
                return toString()
            }
        }

        data class FileTooLargeError(
            val fileSize: Long,
            val expectedFileSize: Long,
            override val id: String? = null,
        ) : Error {
            override fun key(vararg args: Any): Any {
                return toString()
            }
        }
    }
}