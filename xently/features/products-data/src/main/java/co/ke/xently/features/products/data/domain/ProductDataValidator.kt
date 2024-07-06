package co.ke.xently.features.products.data.domain

import co.ke.xently.features.products.data.domain.error.DescriptionError
import co.ke.xently.features.products.data.domain.error.NameError
import co.ke.xently.features.products.data.domain.error.PriceError
import co.ke.xently.features.products.data.domain.error.Result
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductDataValidator @Inject constructor() {
    fun validatedName(name: String): Result<String, NameError> {
        val cleanName = name.trim()

        if (cleanName.isBlank()) {
            return Result.Failure(NameError.MISSING)
        }

        return Result.Success(cleanName)
    }

    fun validatedPrice(price: String): Result<Double, PriceError> {
        val cleanPrice = price.trim().replace(",", "")

        if (cleanPrice.isBlank()) return Result.Failure(PriceError.INVALID)

        val priceDouble = cleanPrice.toDoubleOrNull()
            ?: return Result.Failure(PriceError.INVALID)

        if (priceDouble <= 0) return Result.Failure(PriceError.ZERO_OR_LESS)

        return Result.Success(priceDouble)
    }

    fun validatedDescription(description: String): Result<String?, DescriptionError> {
        val cleanDescription = description.trim()

        if (cleanDescription.isBlank()) return Result.Success(null)

        return Result.Success(cleanDescription)
    }
}