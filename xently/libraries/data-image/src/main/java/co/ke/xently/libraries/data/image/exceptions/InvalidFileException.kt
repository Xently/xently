package co.ke.xently.libraries.data.image.exceptions

class InvalidFileException(override val message: String? = "Invalid file") :
    RuntimeException(message)