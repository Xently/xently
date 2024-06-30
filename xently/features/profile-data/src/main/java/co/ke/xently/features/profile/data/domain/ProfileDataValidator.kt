package co.ke.xently.features.profile.data.domain

import co.ke.xently.features.profile.data.domain.error.NameError
import co.ke.xently.features.profile.data.domain.error.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileDataValidator @Inject constructor() {
    fun validatedName(name: String): Result<String, NameError> {
        val cleanName = name.trim()

        if (cleanName.isBlank()) {
            return Result.Failure(NameError.MISSING)
        }

        return Result.Success(cleanName)
    }
}