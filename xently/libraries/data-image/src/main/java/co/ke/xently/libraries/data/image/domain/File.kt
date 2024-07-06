package co.ke.xently.libraries.data.image.domain


sealed interface File {
    sealed interface Error : File {
        data object InvalidFile : Error

        data class FileTooLarge(
            val fileSize: Long,
            val expectedFileSize: Long,
        ) : Error
    }
}