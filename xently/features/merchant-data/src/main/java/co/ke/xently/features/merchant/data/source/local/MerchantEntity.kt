package co.ke.xently.features.merchant.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import co.ke.xently.features.merchant.data.domain.Merchant

@Entity(tableName = "merchants")
data class MerchantEntity(
    val merchant: Merchant,
    @PrimaryKey
    val id: Long = merchant.id,
)
