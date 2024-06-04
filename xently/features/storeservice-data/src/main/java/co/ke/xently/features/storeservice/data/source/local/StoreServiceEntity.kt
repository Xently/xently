package co.ke.xently.features.storeservice.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import co.ke.xently.features.storeservice.data.domain.StoreService

@Entity(tableName = "store_services")
data class StoreServiceEntity(
    val storeService: StoreService,
    @PrimaryKey
    val name: String = storeService.name,
)
