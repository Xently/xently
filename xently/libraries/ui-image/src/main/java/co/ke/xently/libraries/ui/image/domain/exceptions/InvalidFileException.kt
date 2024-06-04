package co.ke.xently.libraries.ui.image.domain.exceptions

class InvalidFileException(override val message: String? = "Invalid file") :
    RuntimeException(message)