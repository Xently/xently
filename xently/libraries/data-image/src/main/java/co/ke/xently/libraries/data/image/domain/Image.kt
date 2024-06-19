package co.ke.xently.libraries.data.image.domain


sealed interface Image {
    sealed interface Error : Image {
        data object InvalidFileError : Error

        data class FileTooLargeError(
            val fileSize: Long,
            val expectedFileSize: Long,
        ) : Error
    }
}