package com.kwanzatukule.features.customer.complaints.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kwanzatukule.features.customer.complaints.domain.CustomerComplaint


@Entity(tableName = "customer_complaints")
data class CustomerComplaintEntity(
    val customer: CustomerComplaint,
    @PrimaryKey
    val id: Long = customer.id,
)