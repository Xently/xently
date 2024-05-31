package com.kwanzatukule.libraries.data.customer.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kwanzatukule.libraries.data.customer.domain.Customer


@Entity(tableName = "customers")
data class CustomerEntity(
    val customer: Customer,
    @PrimaryKey
    val id: Long = customer.id,
)