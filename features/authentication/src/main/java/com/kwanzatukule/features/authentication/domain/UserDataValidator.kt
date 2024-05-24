package com.kwanzatukule.features.authentication.domain

import com.kwanzatukule.features.authentication.domain.error.PasswordError
import com.kwanzatukule.features.authentication.domain.error.Result

class UserDataValidator {
    fun validatePassword(password: String): Result<Unit, PasswordError> {
        if (password.length < 9) {
            return Result.Failure(PasswordError.TOO_SHORT)
        }

        val hasUppercaseChar = password.any { it.isUpperCase() }
        if (!hasUppercaseChar) {
            return Result.Failure(PasswordError.NO_UPPERCASE)
        }

        val hasDigit = password.any { it.isDigit() }
        if (!hasDigit) {
            return Result.Failure(PasswordError.NO_DIGIT)
        }

        return Result.Success(Unit)
    }
}