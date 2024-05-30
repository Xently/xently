package com.kwanzatukule.features.customer.complaints.presentation.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import co.ke.xently.libraries.ui.core.XentlyPreview
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.kwanzatukule.features.core.presentation.theme.KwanzaTukuleTheme
import com.kwanzatukule.features.customer.complaints.domain.CustomerComplaint
import com.kwanzatukule.features.customer.complaints.domain.error.DataError
import com.kwanzatukule.features.customer.complaints.presentation.list.components.CustomerComplaintListItem
import com.kwanzatukule.libraries.pagination.presentation.PaginatedLazyColumn
import kotlin.random.Random

@Composable
fun CustomerComplaintListScreen(
    modifier: Modifier,
    component: CustomerComplaintListComponent,
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    topBar: @Composable () -> Unit = {},
) {
    val state by component.uiState.subscribeAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        component.event.collect {
            when (it) {
                is CustomerComplaintListEvent.Error -> {
                    val result = snackbarHostState.showSnackbar(
                        it.error.asString(context = context),
                        duration = SnackbarDuration.Long,
                        actionLabel = if (it.type is DataError.Network) "Retry" else null,
                    )

                    when (result) {
                        SnackbarResult.Dismissed -> {

                        }

                        SnackbarResult.ActionPerformed -> {

                        }
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = topBar,
        contentWindowInsets = contentWindowInsets,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            val canAddComplaint = LocalCanAddComplaint.current
            if (canAddComplaint) {
                ExtendedFloatingActionButton(
                    onClick = component::onClickCustomerComplaintEntry,
                    text = { Text(text = "Add complaint") },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.AddComment,
                            contentDescription = "Add complaint",
                        )
                    },
                )
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AnimatedVisibility(state.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            val customers = component.customers.collectAsLazyPagingItems()
            val errorStateContent: @Composable ColumnScope.(Throwable) -> Unit = {
                Text(text = it.message!!)
                Button(onClick = customers::retry) {
                    Text(text = "Retry")
                }
            }
            PaginatedLazyColumn(
                items = customers,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                emptyContentMessage = "No customers",
                errorStateContent = errorStateContent,
                appendErrorStateContent = errorStateContent,
                prependErrorStateContent = errorStateContent,
            ) {
                items(customers.itemCount, key = { customers[it]!!.id }) { index ->
                    val customer = customers[index]!!
                    CustomerComplaintListItem(customer = customer)
                }
            }
        }
    }
}

private data class CustomerComplaintListContent(
    val uiState: CustomerComplaintListUiState,
    val customers: PagingData<CustomerComplaint> = PagingData.empty(),
)

private class CustomerComplaintListContentParameterProvider :
    PreviewParameterProvider<CustomerComplaintListContent> {
    override val values: Sequence<CustomerComplaintListContent>
        get() {
            val customers = List(10) {
                CustomerComplaint(
                    name = "Kibera",
                    email = "customer.${it + 1}@example.com",
                    phone = "+2547123456${Random.nextInt(10, 99)}",
                    id = it.toLong() + 1,
                )
            }
            return sequenceOf(
                CustomerComplaintListContent(
                    uiState = CustomerComplaintListUiState(),
                ),
                CustomerComplaintListContent(
                    uiState = CustomerComplaintListUiState(
                        isLoading = true,
                    ),
                ),
                CustomerComplaintListContent(
                    uiState = CustomerComplaintListUiState(
                        isLoading = true,
                    ),
                    customers = PagingData.empty(
                        LoadStates(
                            LoadState.Loading,
                            LoadState.Loading,
                            LoadState.Loading,
                        ),
                    ),
                ),
                CustomerComplaintListContent(
                    uiState = CustomerComplaintListUiState(
                        isLoading = true,
                    ),
                    customers = PagingData.from(
                        customers,
                        LoadStates(
                            LoadState.NotLoading(true),
                            LoadState.NotLoading(true),
                            LoadState.NotLoading(true),
                        ),
                    ),
                ),
                CustomerComplaintListContent(
                    uiState = CustomerComplaintListUiState(
                        isLoading = true,
                    ),
                    customers = PagingData.empty(
                        LoadStates(
                            LoadState.Error(RuntimeException("Example message...")),
                            LoadState.Error(RuntimeException("Example message...")),
                            LoadState.Error(RuntimeException("Example message...")),
                        ),
                    ),
                ),
            )
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@XentlyPreview
@Composable
private fun CustomerComplaintListScreenPreview(
    @PreviewParameter(CustomerComplaintListContentParameterProvider::class)
    content: CustomerComplaintListContent,
) {
    KwanzaTukuleTheme {
        CustomerComplaintListScreen(
            component = CustomerComplaintListComponent.Fake(
                state = content.uiState,
                _customers = content.customers,
            ),
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text(text = "Customer complaints") },
                    navigationIcon = {
                        IconButton(onClick = {}) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Go back",
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                            )
                        }
                    }
                )
            },
        )
    }
}