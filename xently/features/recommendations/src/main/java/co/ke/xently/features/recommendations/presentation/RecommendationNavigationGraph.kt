package co.ke.xently.features.recommendations.presentation

import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.toUpperCase
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import co.ke.xently.features.products.presentation.list.CategoryFilterableProductListContent
import co.ke.xently.features.products.presentation.list.ProductListViewModel
import co.ke.xently.features.products.presentation.list.components.ProductListItem
import co.ke.xently.features.recommendations.R
import co.ke.xently.features.recommendations.domain.RecommendationNavGraph
import co.ke.xently.features.recommendations.domain.RecommendationNavGraph.RecommendationDetailsScreen
import co.ke.xently.features.recommendations.domain.RecommendationNavGraph.RecommendationRequestScreen
import co.ke.xently.features.recommendations.domain.RecommendationNavGraph.RecommendationResponseScreen
import co.ke.xently.features.recommendations.presentation.details.RecommendationDetailsViewModel
import co.ke.xently.features.recommendations.presentation.request.RecommendationRequestScreen
import co.ke.xently.features.recommendations.presentation.response.RecommendationResponseScreen
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.stores.presentation.detail.StoreDetailScreen

fun NavGraphBuilder.recommendationNavigation(
    navController: NavHostController,
    onClickMoreDetails: (Store) -> Unit,
    onClickReviewStore: (String) -> Unit,
) {
    navigation<RecommendationNavGraph>(startDestination = RecommendationRequestScreen) {
        composable<RecommendationRequestScreen> {
            val entry = remember(it) {
                navController.getBackStackEntry(RecommendationNavGraph)
            }
            val viewModel = hiltViewModel<RecommendationViewModel>(entry)

            RecommendationRequestScreen(
                viewModel = viewModel,
                onClickBack = navController::navigateUp,
                onClickSearch = {
                    navController.navigate(RecommendationResponseScreen)
                },
            )
        }
        composable<RecommendationResponseScreen> { navBackStackEntry ->
            val entry = remember(navBackStackEntry) {
                navController.getBackStackEntry(RecommendationNavGraph)
            }
            val viewModel = hiltViewModel<RecommendationViewModel>(entry)

            RecommendationResponseScreen(
                viewModel = viewModel,
                onClickBack = navController::navigateUp,
                onClickRecommendation = {
                    navController.navigate(
                        RecommendationDetailsScreen(
                            recommendationId = it.id,
                            productsUrl = it.links["products"]!!.hrefWithoutQueryParamTemplates(),
                        )
                    )
                },
            )
        }
        composable<RecommendationDetailsScreen> {
            val viewModel = hiltViewModel<RecommendationDetailsViewModel>()

            val recommendation by viewModel.recommendation.collectAsStateWithLifecycle()
            StoreDetailScreen(
                viewModel = viewModel,
                onClickBack = navController::navigateUp,
                onClickMoreDetails = onClickMoreDetails,
                onClickReviewStore = onClickReviewStore,
            ) {
                val productListViewModel = hiltViewModel<ProductListViewModel>()

                CategoryFilterableProductListContent(
                    viewModel = productListViewModel,
                    modifier = Modifier.matchParentSize(),
                ) {
                    recommendation?.let { recommendationResponse ->
                        if (recommendationResponse.hit.items.isNotEmpty()) {
                            item(
                                key = "best-matched-products-headline",
                                contentType = { "best-matched-products-headline" },
                            ) {
                                ListItem(
                                    headlineContent = {
                                        Text(
                                            text = stringResource(R.string.headline_matched_products).toUpperCase(
                                                Locale.current
                                            ),
                                            textDecoration = TextDecoration.Underline,
                                            style = MaterialTheme.typography.titleMedium,
                                        )
                                    },
                                )
                            }
                        }

                        items(
                            recommendationResponse.hit.items,
                            key = { "best-matched-products-${it.bestMatched.id}" },
                            contentType = { "best-matched-products" },
                        ) { ProductListItem(product = it.bestMatched) }

                        if (recommendationResponse.miss.items.isNotEmpty()) {
                            item(
                                key = "missed-products-headline",
                                contentType = { "missed-products-headline" },
                            ) {
                                ListItem(
                                    headlineContent = {
                                        Text(
                                            text = stringResource(R.string.headline_missed_products).toUpperCase(
                                                Locale.current
                                            ),
                                            textDecoration = TextDecoration.Underline,
                                            style = MaterialTheme.typography.titleMedium,
                                        )
                                    },
                                )
                            }
                        }

                        items(
                            recommendationResponse.miss.items,
                            key = { "missed-products${it.value}" },
                            contentType = { "missed-products" },
                        ) { ListItem(headlineContent = { Text(text = it.value) }) }

                        item(
                            key = "full-catalogue-headline",
                            contentType = { "full-catalogue-headline" },
                        ) {
                            ListItem(
                                headlineContent = {
                                    Text(
                                        text = stringResource(R.string.headline_full_catalogue).toUpperCase(
                                            Locale.current
                                        ),
                                        textDecoration = TextDecoration.Underline,
                                        style = MaterialTheme.typography.titleMedium,
                                    )
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}