package com.kwanzatukule.features.customer.home.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import co.ke.xently.libraries.ui.core.XentlyPreview
import com.kwanzatukule.features.catalogue.domain.Category
import com.kwanzatukule.features.catalogue.domain.Product
import com.kwanzatukule.features.catalogue.presentation.components.CategoryLazyRow
import com.kwanzatukule.features.catalogue.presentation.components.ProductCard
import co.ke.xently.libraries.ui.core.LocalAuthenticationState
import com.kwanzatukule.features.core.presentation.theme.KwanzaTukuleTheme
import com.kwanzatukule.features.customer.home.R
import com.kwanzatukule.features.customer.home.data.Advert
import com.kwanzatukule.features.customer.home.presentation.components.AdvertCard
import com.kwanzatukule.features.customer.home.presentation.components.SuggestedProductListCard
import com.kwanzatukule.features.customer.home.presentation.components.TitledSection
import com.kwanzatukule.libraries.pagination.presentation.PaginatedLazyVerticalGrid

@Composable
fun HomeScreen(component: HomeComponent, modifier: Modifier = Modifier) {
    val advert by component.advert.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        val authenticationState = LocalAuthenticationState.current
        AnimatedVisibility(visible = authenticationState.isSignOutInProgress) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
        val categories = component.paginatedCategories.collectAsLazyPagingItems()
        val featuredProducts = component.paginatedFeaturedProducts.collectAsLazyPagingItems()
        val suggestedProducts = component.paginatedSuggestedProducts.collectAsLazyPagingItems()
        val errorStateContent: @Composable ColumnScope.(Throwable) -> Unit = {
            Text(text = it.message!!)
            Button(onClick = featuredProducts::retry) {
                Text(text = "Retry")
            }
        }
        PaginatedLazyVerticalGrid(
            items = featuredProducts,
            modifier = Modifier.weight(1f),
            columns = GridCells.Adaptive(150.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            emptyContentMessage = "No products",
            errorStateContent = errorStateContent,
            appendErrorStateContent = errorStateContent,
            prependErrorStateContent = errorStateContent,
        ) {
            item(key = "categories", span = { GridItemSpan(maxLineSpan) }) {
                TitledSection(title = stringResource(R.string.section_title_categories)) {
                    CategoryLazyRow(
                        categories = categories,
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        onClick = component::navigateToCatalogue,
                    )
                }
            }
            if (advert != null) {
                item(key = "advert", span = { GridItemSpan(maxLineSpan) }) {
                    AdvertCard(
                        advert = advert!!,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
            }
            item(key = "suggested-products", span = { GridItemSpan(maxLineSpan) }) {
                SuggestedProductListCard(
                    products = suggestedProducts,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    onClick = component::navigateToProductDetail,
                    addToOrRemoveFromShoppingCart = component::addToOrRemoveFromShoppingCart,
                )
            }
            items(featuredProducts.itemCount, key = { featuredProducts[it]!!.id }) { index ->
                val product = featuredProducts[index]!!
                ProductCard(
                    modifier = Modifier
                        .width(IntrinsicSize.Max)
                        .padding(PaddingValues(horizontal = 16.dp)),
                    product = product,
                    onClick = { component.navigateToProductDetail(product) },
                    addToOrRemoveFromShoppingCart = {
                        component.addToOrRemoveFromShoppingCart(
                            product.copy(inShoppingCart = it)
                        )
                    },
                )
            }
        }
    }
}


private data class HomeContent(
    val advert: Advert? = null,
    val categories: PagingData<Category> = PagingData.empty(),
    val suggestedProducts: PagingData<Product> = PagingData.empty(),
    val featuredProducts: PagingData<Product> = PagingData.empty(),
)

private class HomeContentParameterProvider : PreviewParameterProvider<HomeContent> {
    override val values: Sequence<HomeContent>
        get() {
            val categories = listOf(
                Category(
                    name = "Bevarage",
                    image = "https://hotpoint.co.ke/media/cache/3b/ac/3bac8cc2023794721ab87177abfb049d.webp",
                ),
                Category(
                    name = "Sugar",
                    image = "https://hotpoint.co.ke/media/images/HVAC_Clearance_Sale_Apri.width-600.format-webp-lossless.webp",
                ),
                Category(
                    name = "Flour",
                    image = "https://hotpoint.co.ke/media/images/Generic_KV.width-600.format-webp-lossless.webp",
                ),
                Category(
                    name = "Cooking Oil",
                    image = "https://hotpoint.co.ke/media/images/BUILT_IN_SALE.width-600.format-webp-lossless.webp",
                ),
                Category(
                    name = "Category Name",
                    image = "https://hotpoint.co.ke/media/images/tvs_n_audio.width-600.format-webp-lossless.webp",
                ),
                Category(
                    name = "Category Name",
                    image = "https://hotpoint.co.ke/media/images/fridges_n_freezers.width-600.format-webp-lossless.webp",
                ),
            ).mapIndexed { index, category -> category.copy(id = (index + 1).toLong()) }
            val products = listOf(
                Product(
                    name = "Product Name",
                    price = 100,
                    image = "https://hotpoint.co.ke/media/cache/3d/70/3d7013f4afb806bf3bdfa7e1002d5c66@2x.webp",
                ),
                Product(
                    name = "Product Name",
                    price = 200,
                    image = "https://hotpoint.co.ke/media/cache/13/ef/13efddfbcb3709fd76c31ee16e631be6@2x.webp",
                ),
                Product(
                    name = "Product Name",
                    price = 2_080,
                    image = "https://hotpoint.co.ke/media/cache/15/68/1568cc5249ebf8a84ca6e2cbeb6610e4@2x.webp",
                ),
                Product(
                    name = "Product Name",
                    price = 10_800,
                    image = "https://hotpoint.co.ke/media/cache/b5/b0/b5b07cd8fa5c962021ee048c6ffffcba@2x.webp",
                ),
                Product(
                    name = "Product Name",
                    price = 280,
                    image = "https://hotpoint.co.ke/media/cache/3d/70/3d7013f4afb806bf3bdfa7e1002d5c66@2x.webp",
                ),
                Product(
                    name = "Product Name",
                    price = 80,
                    image = "https://hotpoint.co.ke/media/cache/87/af/87affefd47f37424b57efb0c1e5c34dc@2x.webp",
                ),
            ).mapIndexed { index, product -> product.copy(id = (index + 1).toLong()) }
            return sequenceOf(
                HomeContent(
                    advert = Advert(
                        title = "Today's Deal",
                        subtitle = "20% off on all products",
                        headline = "KES. 80",
                        image = "https://hotpoint.co.ke/media/images/Buy_Now.width-600.format.width-600.format-webp-lossless.webp",
                    ),
                    categories = PagingData.from(
                        categories,
                        LoadStates(
                            LoadState.NotLoading(true),
                            LoadState.NotLoading(true),
                            LoadState.NotLoading(true),
                        ),
                    ),
                    featuredProducts = PagingData.from(
                        products,
                        LoadStates(
                            LoadState.NotLoading(true),
                            LoadState.NotLoading(true),
                            LoadState.NotLoading(true),
                        ),
                    ),
                    suggestedProducts = PagingData.from(
                        products,
                        LoadStates(
                            LoadState.NotLoading(true),
                            LoadState.NotLoading(true),
                            LoadState.NotLoading(true),
                        ),
                    ),
                ),
                HomeContent(
                    advert = Advert(
                        title = "Today's Deal",
                        subtitle = "20% off on all products",
                        headline = "KES. 80",
                        image = "https://hotpoint.co.ke/media/images/Buy_Now.width-600.format.width-600.format-webp-lossless.webp",
                    ),
                    categories = PagingData.empty(
                        LoadStates(
                            LoadState.Loading,
                            LoadState.Loading,
                            LoadState.Loading,
                        ),
                    ),
                    featuredProducts = PagingData.from(
                        products,
                        LoadStates(
                            LoadState.NotLoading(true),
                            LoadState.NotLoading(true),
                            LoadState.NotLoading(true),
                        ),
                    ),
                    suggestedProducts = PagingData.from(
                        products,
                        LoadStates(
                            LoadState.NotLoading(true),
                            LoadState.NotLoading(true),
                            LoadState.NotLoading(true),
                        ),
                    ),
                ),
                HomeContent(
                    advert = Advert(
                        title = "Today's Deal",
                        subtitle = "20% off on all products",
                        headline = "KES. 80",
                        image = "https://hotpoint.co.ke/media/images/Buy_Now.width-600.format.width-600.format-webp-lossless.webp",
                    ),
                    categories = PagingData.empty(
                        LoadStates(
                            LoadState.Error(RuntimeException("Example message...")),
                            LoadState.Error(RuntimeException("Example message...")),
                            LoadState.Error(RuntimeException("Example message...")),
                        ),
                    ),
                    featuredProducts = PagingData.from(
                        products,
                        LoadStates(
                            LoadState.NotLoading(true),
                            LoadState.NotLoading(true),
                            LoadState.NotLoading(true),
                        ),
                    ),
                    suggestedProducts = PagingData.from(
                        products,
                        LoadStates(
                            LoadState.NotLoading(true),
                            LoadState.NotLoading(true),
                            LoadState.NotLoading(true),
                        ),
                    ),
                ),
                HomeContent(
                    advert = Advert(
                        title = "Today's Deal",
                        subtitle = "20% off on all products",
                        headline = "KES. 80",
                        image = "https://hotpoint.co.ke/media/images/Buy_Now.width-600.format.width-600.format-webp-lossless.webp",
                    ),
                    categories = PagingData.from(
                        categories,
                        LoadStates(
                            LoadState.NotLoading(true),
                            LoadState.NotLoading(true),
                            LoadState.NotLoading(true),
                        ),
                    ),
                    featuredProducts = PagingData.from(
                        products,
                        LoadStates(
                            LoadState.NotLoading(true),
                            LoadState.NotLoading(true),
                            LoadState.NotLoading(true),
                        ),
                    ),
                    suggestedProducts = PagingData.empty(
                        LoadStates(
                            LoadState.Loading,
                            LoadState.Loading,
                            LoadState.Loading,
                        ),
                    ),
                ),
                HomeContent(
                    advert = Advert(
                        title = "Today's Deal",
                        subtitle = "20% off on all products",
                        headline = "KES. 80",
                        image = "https://hotpoint.co.ke/media/images/Buy_Now.width-600.format.width-600.format-webp-lossless.webp",
                    ),
                    categories = PagingData.from(
                        categories,
                        LoadStates(
                            LoadState.NotLoading(true),
                            LoadState.NotLoading(true),
                            LoadState.NotLoading(true),
                        ),
                    ),
                    featuredProducts = PagingData.from(
                        products,
                        LoadStates(
                            LoadState.NotLoading(true),
                            LoadState.NotLoading(true),
                            LoadState.NotLoading(true),
                        ),
                    ),
                    suggestedProducts = PagingData.empty(
                        LoadStates(
                            LoadState.Error(RuntimeException("Example message...")),
                            LoadState.Error(RuntimeException("Example message...")),
                            LoadState.Error(RuntimeException("Example message...")),
                        ),
                    ),
                ),
                HomeContent(
                    advert = Advert(
                        title = "Today's Deal",
                        subtitle = "20% off on all products",
                        headline = "KES. 80",
                        image = "https://hotpoint.co.ke/media/images/Buy_Now.width-600.format.width-600.format-webp-lossless.webp",
                    ),
                    categories = PagingData.from(
                        categories,
                        LoadStates(
                            LoadState.NotLoading(true),
                            LoadState.NotLoading(true),
                            LoadState.NotLoading(true),
                        ),
                    ),
                    featuredProducts = PagingData.empty(
                        LoadStates(
                            LoadState.Loading,
                            LoadState.Loading,
                            LoadState.Loading,
                        ),
                    ),
                    suggestedProducts = PagingData.from(
                        products,
                        LoadStates(
                            LoadState.NotLoading(true),
                            LoadState.NotLoading(true),
                            LoadState.NotLoading(true),
                        ),
                    ),
                ),
                HomeContent(
                    advert = Advert(
                        title = "Today's Deal",
                        subtitle = "20% off on all products",
                        headline = "KES. 80",
                        image = "https://hotpoint.co.ke/media/images/Buy_Now.width-600.format.width-600.format-webp-lossless.webp",
                    ),
                    categories = PagingData.from(
                        categories,
                        LoadStates(
                            LoadState.NotLoading(true),
                            LoadState.NotLoading(true),
                            LoadState.NotLoading(true),
                        ),
                    ),
                    featuredProducts = PagingData.empty(
                        LoadStates(
                            LoadState.Error(RuntimeException("Example message...")),
                            LoadState.Error(RuntimeException("Example message...")),
                            LoadState.Error(RuntimeException("Example message...")),
                        ),
                    ),
                    suggestedProducts = PagingData.from(
                        products,
                        LoadStates(
                            LoadState.NotLoading(true),
                            LoadState.NotLoading(true),
                            LoadState.NotLoading(true),
                        ),
                    ),
                ),
            )
        }
}

@XentlyPreview
@Composable
private fun HomeScreenPreview(
    @PreviewParameter(HomeContentParameterProvider::class)
    content: HomeContent,
) {
    KwanzaTukuleTheme {
        HomeScreen(
            component = HomeComponent.Fake(
                content.advert,
                categories = content.categories,
                suggestedProducts = content.suggestedProducts,
                featuredProducts = content.featuredProducts,
            ),
            modifier = Modifier.fillMaxSize(),
        )
    }
}