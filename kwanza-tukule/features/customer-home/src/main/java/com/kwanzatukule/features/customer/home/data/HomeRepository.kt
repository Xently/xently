package com.kwanzatukule.features.customer.home.data

import com.kwanzatukule.features.cart.data.ShoppingCartRepository
import com.kwanzatukule.features.catalogue.data.CatalogueRepository
import kotlinx.coroutines.flow.Flow

interface HomeRepository : CatalogueRepository, ShoppingCartRepository {
    fun getAdvert(): Flow<Advert>
}
