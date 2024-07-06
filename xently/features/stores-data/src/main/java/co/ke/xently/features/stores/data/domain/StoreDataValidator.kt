package co.ke.xently.features.stores.data.domain

import android.util.Patterns
import co.ke.xently.features.stores.data.domain.error.EmailError
import co.ke.xently.features.stores.data.domain.error.LocationError
import co.ke.xently.features.stores.data.domain.error.NameError
import co.ke.xently.features.stores.data.domain.error.PhoneError
import co.ke.xently.features.stores.data.domain.error.Result
import co.ke.xently.libraries.location.tracker.domain.Location
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StoreDataValidator @Inject constructor() {
    fun validatedName(name: String): Result<String, NameError> {
        val cleanName = name.trim()

        if (cleanName.isBlank()) {
            return Result.Failure(NameError.MISSING)
        }

        return Result.Success(cleanName)
    }

    fun validatedEmail(email: String): Result<String?, EmailError> {
        val cleanEmail = email.trim()

        if (cleanEmail.isBlank()) return Result.Success(null)

        if (!Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            return Result.Failure(EmailError.INVALID_FORMAT)
        }
        return Result.Success(cleanEmail)
    }

    fun validatedPhone(phone: String): Result<String?, PhoneError> {
        val cleanPhone = phone.trim()

        if (cleanPhone.isBlank()) return Result.Success(null)

        if (!Patterns.PHONE.matcher(cleanPhone).matches()) {
            return Result.Failure(PhoneError.INVALID_FORMAT)
        }
        return Result.Success(cleanPhone)
    }

    fun validatedLocation(location: String): Result<Location, LocationError> {
        if (location.isBlank()) {
            return Result.Failure(LocationError.MISSING)
        }

        val coordinates = location.replace("x=", "")
            .replace("y=", "")
            .split("\\s*[,;|]\\s*".toRegex())
        if (coordinates.size < 2) {
            return Result.Failure(LocationError.INVALID_FORMAT)
        }

        val longitude = coordinates[0].toDoubleOrNull()
            ?: return Result.Failure(LocationError.INVALID_LONGITUDE)

        val latitude = coordinates[1].toDoubleOrNull()
            ?: return Result.Failure(LocationError.INVALID_LATITUDE)

        return Result.Success(Location(latitude = latitude, longitude = longitude))
    }
}