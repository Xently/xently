package co.ke.xently.features.shops.data.domain

import android.util.Patterns
import co.ke.xently.features.shops.data.domain.error.NameError
import co.ke.xently.features.shops.data.domain.error.Result
import co.ke.xently.features.shops.data.domain.error.WebsiteError
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopDataValidator @Inject constructor() {
    fun validatedName(name: String): Result<String, NameError> {
        val cleanName = name.trim()

        if (cleanName.isBlank()) {
            return Result.Failure(NameError.MISSING)
        }

        return Result.Success(cleanName)
    }

    fun validatedWebsite(website: String): Result<String?, WebsiteError> {
        val cleanWebsite = website.trim()

        if (cleanWebsite.isBlank()) return Result.Success(null)

        if (!Patterns.WEB_URL.matcher(cleanWebsite).matches()) {
            return Result.Failure(WebsiteError.INVALID_FORMAT)
        }
        return Result.Success(cleanWebsite)
    }
}