package co.ke.xently.features.customers.presentation.list

import androidx.paging.PagingData
import co.ke.xently.features.customers.data.domain.Customer
import co.ke.xently.features.customers.data.domain.error.ShopSelectionRequiredException
import co.ke.xently.features.customers.data.domain.error.StoreSelectionRequiredException
import co.ke.xently.features.customers.data.source.CustomerRepository
import co.ke.xently.features.stores.data.domain.error.ConfigurationError
import co.ke.xently.features.stores.data.domain.error.Result
import co.ke.xently.features.stores.data.source.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class CustomerListViewModel @Inject constructor(
    repository: CustomerRepository,
    storeRepository: StoreRepository,
) : CustomerScoreboardListViewModel(repository = repository) {
    override val customers: Flow<PagingData<Customer>> =
        storeRepository.findActiveStore().flatMapLatest { result ->
            when (result) {
                is Result.Failure -> {
                    when (result.error) {
                        ConfigurationError.ShopSelectionRequired -> throw ShopSelectionRequiredException()
                        ConfigurationError.StoreSelectionRequired -> throw StoreSelectionRequiredException()
                    }
                }

                is Result.Success -> {
                    val dataUrl = result.data.links["rankings"]!!.hrefWithoutQueryParamTemplates()
                    getCustomerPagingDataFlow(dataUrl = dataUrl)
                }
            }
        }
}