package com.kwanzatukule.features.catalogue.data

import com.kwanzatukule.features.catalogue.domain.Category

data class CatalogueFilters(
    val query: String? = null,
    val category: Category? = null,
)
