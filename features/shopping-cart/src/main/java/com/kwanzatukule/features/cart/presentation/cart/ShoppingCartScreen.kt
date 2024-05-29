package com.kwanzatukule.features.cart.presentation.cart

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.kwanzatukule.features.cart.domain.ShoppingCart
import com.kwanzatukule.features.cart.domain.error.DataError
import com.kwanzatukule.features.cart.presentation.LocalShoppingCartState
import com.kwanzatukule.features.cart.presentation.components.ShoppingCartLineCart
import com.kwanzatukule.features.cart.presentation.components.ShoppingCartTotalBottomBarCard
import com.kwanzatukule.features.catalogue.domain.Product
import com.kwanzatukule.features.core.presentation.KwanzaPreview
import com.kwanzatukule.features.core.presentation.theme.KwanzaTukuleTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingCartScreen(component: ShoppingCartComponent, modifier: Modifier = Modifier) {
    val state by component.uiState.subscribeAsState()

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        component.event.collect {
            when (it) {
                is ShoppingCartEvent.Error -> {
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

    val shoppingCart by LocalShoppingCartState.current

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets.statusBars,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Shopping cart") },
                navigationIcon = {
                    IconButton(onClick = component::handleBackPress) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back",
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search...")
                    }
                },
            )
        },
        bottomBar = {
            ShoppingCartTotalBottomBarCard(
                shoppingCart = shoppingCart,
                submitLabel = "Checkout",
                onClickSubmit = component::handleCheckout,
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            AnimatedVisibility(state.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(shoppingCart.items, key = { it.id }) { item ->
                    var showDeleteDialog by rememberSaveable {
                        mutableStateOf(false)
                    }
                    val dismissState = rememberSwipeToDismissBoxState()

                    if (showDeleteDialog) {
                        val onDismissRequest: () -> Unit = {
                            showDeleteDialog = false
                            scope.launch {
                                dismissState.reset()
                            }
                        }
                        AlertDialog(
                            onDismissRequest = onDismissRequest,
                            title = { Text(text = "Remove from cart") },
                            text = { Text(text = "Are you sure you want to remove this item from your cart?") },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        component.remove(item)
                                        showDeleteDialog = false
                                    },
                                ) { Text(text = "Yes") }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = onDismissRequest,
                                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                                ) { Text(text = "No") }
                            },
                        )
                    }

                    LaunchedEffect(dismissState.currentValue) {
                        if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
                            showDeleteDialog = true
                        }
                    }

                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            val color by animateColorAsState(
                                label = "SwipeDismissAnimation",
                                targetValue = when (dismissState.targetValue) {
                                    SwipeToDismissBoxValue.Settled -> Color.Transparent
                                    SwipeToDismissBoxValue.StartToEnd, SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.error
                                },
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(color),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = if (dismissState.targetValue == SwipeToDismissBoxValue.StartToEnd) {
                                        contentColorFor(color)
                                    } else {
                                        Color.Transparent
                                    },
                                    modifier = Modifier.padding(start = 24.dp),
                                )
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                                        contentColorFor(color)
                                    } else {
                                        Color.Transparent
                                    },
                                    modifier = Modifier.padding(end = 24.dp),
                                )
                            }
                        }
                    ) {
                        ShoppingCartLineCart(
                            item = item,
                            remove = { showDeleteDialog = true },
                            decrementQuantity = { component.decrementQuantity(item) },
                            incrementQuantity = { component.incrementQuantity(item) },
                        )
                    }

                    HorizontalDivider()
                }
            }
        }
    }
}

private class ShoppingCartUiStateParameterProvider : PreviewParameterProvider<ShoppingCartUiState> {
    override val values: Sequence<ShoppingCartUiState>
        get() {
            val shoppingCart = ShoppingCart(
                items = listOf(
                    ShoppingCart.Item(
                        Product(
                            name = "Random product name",
                            price = 1256,
                            image = "https://picsum.photos/200/300",
                        ),
                        1,
                    ),
                    ShoppingCart.Item(
                        Product(
                            name = "Random product name",
                            price = 456,
                            image = "https://picsum.photos/200/300",
                        ),
                        3,
                    ),
                    ShoppingCart.Item(
                        Product(
                            name = "Random product name",
                            price = 234,
                            image = "https://picsum.photos/200/300",
                        ),
                        1,
                    ),
                ).mapIndexed { index, item -> item.copy(id = (index + 1).toLong()) },
            )
            return sequenceOf(
                ShoppingCartUiState(shoppingCart = shoppingCart),
                ShoppingCartUiState(shoppingCart = shoppingCart, isLoading = true),
            )
        }
}

@KwanzaPreview
@Composable
private fun ShoppingCartScreenPreview(
    @PreviewParameter(ShoppingCartUiStateParameterProvider::class)
    uiState: ShoppingCartUiState,
) {
    KwanzaTukuleTheme {
        CompositionLocalProvider(LocalShoppingCartState provides remember { mutableStateOf(uiState.shoppingCart) }) {
            ShoppingCartScreen(
                component = ShoppingCartComponent.Fake(uiState),
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}