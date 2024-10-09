package co.ke.xently.features.stores.data.source

import co.ke.xently.features.storecategory.data.domain.StoreCategory
import co.ke.xently.features.stores.data.domain.StorePaymentMethod
import co.ke.xently.features.storeservice.data.domain.StoreService
import co.ke.xently.libraries.location.tracker.domain.Location
import kotlinx.serialization.Serializable

@Serializable
internal data class StoreSaveRequest(
    val name: String,
    val location: Location,
    val slug: String,
    val telephone: String?,
    val email: String?,
    val description: String?,
    val categories: Set<StoreCategory>,
    val services: List<StoreService>,
    val paymentMethods: List<StorePaymentMethod>,
)