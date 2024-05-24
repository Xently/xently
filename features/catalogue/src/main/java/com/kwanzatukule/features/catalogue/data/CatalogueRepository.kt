package com.kwanzatukule.features.catalogue.data

import com.kwanzatukule.features.catalogue.domain.Category
import com.kwanzatukule.features.catalogue.domain.Product
import com.kwanzatukule.libraries.pagination.domain.models.PagedResponse

interface CatalogueRepository {
    suspend fun getCategories(url: String?): PagedResponse<Category>
    suspend fun getProducts(
        url: String?,
        filters: CatalogueFilters = CatalogueFilters(),
    ): PagedResponse<Product>
}