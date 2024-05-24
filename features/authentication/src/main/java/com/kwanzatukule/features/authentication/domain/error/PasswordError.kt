package com.kwanzatukule.features.authentication.domain.error

enum class PasswordError : Error {
    TOO_SHORT,
    NO_UPPERCASE,
    NO_DIGIT
}