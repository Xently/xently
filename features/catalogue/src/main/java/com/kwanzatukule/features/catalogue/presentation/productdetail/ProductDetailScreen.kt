package com.kwanzatukule.features.catalogue.presentation.productdetail


import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.RemoveShoppingCart
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.kwanzatukule.features.catalogue.domain.Category
import com.kwanzatukule.features.catalogue.domain.Product
import com.kwanzatukule.features.catalogue.domain.error.DataError
import com.kwanzatukule.features.core.presentation.KwanzaPreview
import com.kwanzatukule.features.core.presentation.XentlyAsyncImage
import com.kwanzatukule.features.core.presentation.theme.KwanzaTukuleTheme
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue
import kotlin.random.Random
import kotlin.time.Duration.Companion.seconds


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProductDetailScreen(
    component: ProductDetailComponent,
    modifier: Modifier = Modifier,
    shoppingCartBadge: @Composable () -> Unit,
) {
    val state by component.uiState.subscribeAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        component.event.collect {
            when (it) {
                is ProductDetailEvent.Error -> {
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
//        contentWindowInsets = WindowInsets(0),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            val pagerState = rememberPagerState(
                initialPageOffsetFraction = 0f,
                pageCount = { state.product.images.size },
            )
            LaunchedEffect(pagerState, state.product.images.size) {
                while (state.product.images.isNotEmpty()) {
                    delay(10.seconds)
                    pagerState.animateScrollToPage(
                        (pagerState.currentPage + 1).mod(state.product.images.size)
                    )
                }
            }
            TopAppBar(
                product = state.product,
                pagerState = pagerState,
                windowInsets = WindowInsets.navigationBars, // TopAppBarDefaults.windowInsets
                onClickReviews = {},
                onClickBack = component::handleBackPress,
                addToOrRemoveFromShoppingCart = {
                    component.addToOrRemoveFromShoppingCart(
                        state.product.copy(
                            inShoppingCart = it
                        )
                    )
                },
                shoppingCartBadge = shoppingCartBadge,
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            AnimatedVisibility(state.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            if (!state.product.description.isNullOrBlank()) {
                Text(
                    text = state.product.description.orEmpty(),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun Modifier.animatePageChanges(pagerState: PagerState, index: Int) =
    graphicsLayer {
        val x = (pagerState.currentPage - index + pagerState.currentPageOffsetFraction) * 2
        alpha = 1f - (x.absoluteValue * 0.7f).coerceIn(0f, 0.7f)
        val scale = 1f - (x.absoluteValue * 0.4f).coerceIn(0f, 0.4f)
        scaleX = scale
        scaleY = scale
        rotationY = x * 15f
    }

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
private fun TopAppBar(
    product: Product,
    pagerState: PagerState,
    windowInsets: WindowInsets,
    onClickReviews: () -> Unit,
    onClickBack: () -> Unit,
    addToOrRemoveFromShoppingCart: (Boolean) -> Unit,
    shoppingCartBadge: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .windowInsetsPadding(windowInsets)
            // clip after padding so we don't show the title over the inset area
            .clipToBounds(),
    ) {
        Card(shape = RectangleShape) {
            Box(
                contentAlignment = Alignment.BottomCenter,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(270.dp),
            ) {
                HorizontalPager(state = pagerState) { index ->
                    XentlyAsyncImage(
                        contentScale = ContentScale.FillBounds,
                        data = product.images[index],
                        modifier = Modifier
                            .fillMaxSize()
                            .animatePageChanges(pagerState, index),
                    )
                }
                Row(
                    modifier = Modifier.padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    for (index in product.images.indices) {
                        Icon(
                            Icons.Default.Circle,
                            contentDescription = null,
                            modifier = Modifier.size(10.dp),
                            tint = if (pagerState.currentPage == index) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                LocalContentColor.current.copy(alpha = 0.3f)
                            },
                        )
                    }
                }
            }

            ListItem(
                colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                headlineContent = {
                    Text(
                        text = product.name,
                        modifier = Modifier.basicMarquee(),
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                trailingContent = {
                    var checked by remember(product.inShoppingCart) {
                        mutableStateOf(product.inShoppingCart)
                    }
                    FilledIconToggleButton(
                        checked = checked,
                        onCheckedChange = { checked = it; addToOrRemoveFromShoppingCart(it) },
                        colors = IconButtonDefaults.filledIconToggleButtonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = contentColorFor(backgroundColor = MaterialTheme.colorScheme.secondary),
                            checkedContainerColor = MaterialTheme.colorScheme.error,
                            checkedContentColor = contentColorFor(backgroundColor = MaterialTheme.colorScheme.error),
                        ),
                    ) {
                        AnimatedContent(
                            targetState = checked,
                            label = "Add or remove product from shopping cart",
                        ) { inShoppingCart ->
                            if (inShoppingCart) {
                                Icon(
                                    Icons.Default.RemoveShoppingCart,
                                    contentDescription = "Remove ${product.name} from shopping cart",
                                )
                            } else {
                                Icon(
                                    Icons.Default.AddShoppingCart,
                                    contentDescription = "Add ${product.name} to shopping cart",
                                )
                            }
                        }
                    }
                },
            )

            AnimatedVisibility(
                visible = remember {
                    derivedStateOf {
                        product.categories.isNotEmpty()
                    }
                }.value,
            ) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(product.categories, key = { it.id }) {
                        AssistChip(
                            onClick = { /*TODO*/ },
                            label = { Text(text = it.name) },
                        )
                    }
                }
            }

            TextButton(
                modifier = Modifier.padding(horizontal = 16.dp),
                onClick = onClickReviews,
                contentPadding = PaddingValues(vertical = 12.dp),
                content = {
                    Text(
                        text = "Reviews",
                        textDecoration = TextDecoration.Underline,
                    )
                },
                shape = RoundedCornerShape(20),
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onBackground),
            )
        }
        val iconButtonColors = IconButtonDefaults.filledIconButtonColors()
        TopAppBar(
            title = { /*TODO*/ },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                actionIconContentColor = iconButtonColors.containerColor,
                navigationIconContentColor = iconButtonColors.containerColor,
            ),
            navigationIcon = {
                IconButton(onClick = onClickBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go back",
                    )
                }
            },
            actions = {
                shoppingCartBadge()
            },
        )
    }
}

private class ProductDetailUiStateParameterProvider :
    PreviewParameterProvider<ProductDetailUiState> {
    override val values: Sequence<ProductDetailUiState>
        get() {
            val product = Product(
                name = "Random product name",
                price = 123456,
                images = List(5) { "https://picsum.photos/200/300" },
                categories = List(10) {
                    Category(
                        id = (it + 1).toLong(),
                        name = "Category ${it + 1}",
                    )
                },
                description = """The Material Research Team conducted two studies (quantitative and qualitative) with over 200 participants to understand their perspectives of five different carousel designs. The studies measured their understanding of how to interact with each carousel, their expectations of the number of items in each design, and how they expected carousels to be used.

Summary of findings:

Participants thought carousels were a good way to explore many different types of content.
A previewed or squished item strongly indicated that there was more content to swipe through.
Participants expected around 10 items in a carousel that scrolled multiple items at once.
While some contexts were considered better for some carousel designs, all designs were considered similarly usable.""",
            )
            return sequenceOf(
                ProductDetailUiState(product = product),
                ProductDetailUiState(product = product, isLoading = true),
            )
        }
}

@KwanzaPreview
@Composable
private fun ProductDetailScreenPreview(
    @PreviewParameter(ProductDetailUiStateParameterProvider::class)
    uiState: ProductDetailUiState,
) {
    KwanzaTukuleTheme {
        ProductDetailScreen(
            component = ProductDetailComponent.Fake(uiState),
            modifier = Modifier.fillMaxSize(),
            shoppingCartBadge = {
                BadgedBox(
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .clickable(
                            onClick = { },
                            enabled = Random.nextBoolean(),
                            role = Role.Button,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(
                                bounded = false,
                                radius = 40.dp / 2
                            ),
                        ),
                    content = {
                        Icon(
                            Icons.Default.ShoppingCart,
                            contentDescription = "Open shopping cart",
                        )
                    },
                    badge = {
                        Badge {
                            val numberOfItems = "2389"
                            Text(
                                numberOfItems,
                                modifier = Modifier.semantics {
                                    contentDescription =
                                        "$numberOfItems items in the shopping cart"
                                }
                            )
                        }
                    },
                )
            },
        )
    }
}