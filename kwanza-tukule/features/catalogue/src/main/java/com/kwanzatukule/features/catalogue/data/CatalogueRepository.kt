package com.kwanzatukule.features.catalogue.data

import co.ke.xently.libraries.pagination.domain.models.PagedResponse
import com.kwanzatukule.features.catalogue.domain.Category
import com.kwanzatukule.features.catalogue.domain.Product

interface CatalogueRepository {
    suspend fun getCategories(url: String?): PagedResponse<Category>
    suspend fun getProducts(
        url: String?,
        filters: CatalogueFilters = CatalogueFilters(),
    ): PagedResponse<Product>
}