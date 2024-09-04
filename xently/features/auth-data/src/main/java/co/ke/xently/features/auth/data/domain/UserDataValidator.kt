package co.ke.xently.features.auth.data.domain

import android.util.Patterns
import co.ke.xently.features.auth.data.domain.error.EmailError
import co.ke.xently.features.auth.data.domain.error.NameError
import co.ke.xently.features.auth.data.domain.error.PasswordError
import co.ke.xently.features.auth.data.domain.error.Result
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class UserDataValidator @Inject constructor() {
    fun validatedName(name: String): Result<Name, NameError> {
        val cleanName = name.trim()

        if (cleanName.isBlank()) {
            return Result.Failure(NameError.MISSING)
        }

        val names = cleanName.split("\\s+".toRegex(), limit = 2)
        if (names.size < 2) {
            return Result.Failure(NameError.MISSING_LAST_NAME)
        }

        val firstName = names.firstOrNull()?.takeIf(String::isNotBlank)
        val lastName = names.lastOrNull()?.takeIf(String::isNotBlank)

        return Result.Success(Name(firstName = firstName, lastName = lastName))
    }

    fun validatedEmail(email: String): Result<String, EmailError> {
        val cleanEmail = email.trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            return Result.Failure(EmailError.INVALID_FORMAT)
        }
        return Result.Success(cleanEmail)
    }

    fun validatedPassword(password: String): Result<String, PasswordError> {
        if (password.length < 6) {
            return Result.Failure(PasswordError.TooShort(6))
        }

        /*val hasUppercaseChar = password.any { it.isUpperCase() }
        if (!hasUppercaseChar) {
            return Result.Failure(PasswordError.NoUpperCase)
        }

        val hasDigit = password.any { it.isDigit() }
        if (!hasDigit) {
            return Result.Failure(PasswordError.NoDigit)
        }*/

        return Result.Success(password)
    }
}