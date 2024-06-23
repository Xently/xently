package co.ke.xently.features.reviews.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import co.ke.xently.features.reviewcategory.data.domain.ReviewCategory
import co.ke.xently.features.reviewcategory.data.domain.error.ConfigurationError
import co.ke.xently.features.reviewcategory.data.domain.error.DataError.Network
import co.ke.xently.features.reviewcategory.presentation.components.ReviewCategoryItem
import co.ke.xently.features.reviewcategory.presentation.utils.UiText
import co.ke.xently.features.reviews.R
import co.ke.xently.features.reviews.presentation.reviews.ReviewCategoriesResponse
import co.ke.xently.features.ui.core.presentation.LocalEventHandler
import co.ke.xently.features.ui.core.presentation.components.LoginAndRetryButtonsRow
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyThemePreview
import co.ke.xently.libraries.ui.core.components.shimmer

@Composable
internal fun ReviewCategoryListSection(
    response: ReviewCategoriesResponse,
    selectedCategory: ReviewCategory?,
    modifier: Modifier = Modifier,
    onClickRetry: () -> Unit,
    onClickAddNewReviewCategory: () -> Unit,
    onClickSelectCategory: (ReviewCategory) -> Unit,
    onClickMoreCategoryOptions: (ReviewCategory) -> Unit,
) {
    val eventHandler = LocalEventHandler.current
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        UnderlinedHeadline(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            headline = stringResource(R.string.section_title_review_category),
            trailingContent = {
                Surface(
                    content = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.action_add_review_category),
                        )
                    },
                    onClick = onClickAddNewReviewCategory,
                    color = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                )
            },
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            when (response) {
                is ReviewCategoriesResponse.Failure -> {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = response.error.asString(), modifier = Modifier.weight(1f))

                        when (response.type) {
                            ConfigurationError.ShopSelectionRequired -> {
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = eventHandler::requestShopSelection) {
                                    Text(text = stringResource(R.string.action_select_shop))
                                }
                            }

                            ConfigurationError.StoreSelectionRequired -> {
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(onClick = eventHandler::requestStoreSelection) {
                                    Text(text = stringResource(R.string.action_select_store))
                                }
                            }

                            is Network.Unauthorized -> {
                                Spacer(modifier = Modifier.height(16.dp))

                                LoginAndRetryButtonsRow(onRetry = onClickRetry)
                            }

                            else -> {
                                Button(onClick = onClickRetry) {
                                    Text(text = stringResource(R.string.action_retry))
                                }
                            }
                        }
                    }
                }

                ReviewCategoriesResponse.Loading -> {
                    val categories = remember {
                        listOf(
                            ReviewCategory(name = "Staff friendliness"),
                            ReviewCategory(name = "Ambience"),
                            ReviewCategory(name = "Cleanliness"),
                        ).shuffled()
                    }
                    for (category in categories) {
                        ReviewCategoryItem(
                            modifier = Modifier.shimmer(true),
                            category = category,
                            selected = selectedCategory?.name == category.name,
                            onClick = { },
                            onClickMoreOptions = { },
                        )
                    }
                }

                ReviewCategoriesResponse.Success.Empty -> {
                    Text(text = stringResource(R.string.message_no_review_categories))
                }

                is ReviewCategoriesResponse.Success.NonEmpty -> {
                    for (category in response.data) {
                        ReviewCategoryItem(
                            modifier = Modifier,
                            category = category,
                            selected = selectedCategory?.name == category.name,
                            onClick = { onClickSelectCategory(category) },
                            onClickMoreOptions = { onClickMoreCategoryOptions(category) },
                        )
                    }
                }
            }
        }
    }
}

private class ReviewCategoryListSectionPreviewParameterProvider :
    PreviewParameterProvider<ReviewCategoriesResponse> {
    override val values: Sequence<ReviewCategoriesResponse>
        get() = sequenceOf(
            ReviewCategoriesResponse.Loading,
            ReviewCategoriesResponse.Failure(
                error = UiText.DynamicString("Sample error message"),
                type = Network.Retryable.Unknown,
            ),
            ReviewCategoriesResponse.Success.Empty,
            ReviewCategoriesResponse.Success.NonEmpty(
                listOf(
                    ReviewCategory(name = "Staff friendliness"),
                    ReviewCategory(name = "Ambience"),
                    ReviewCategory(name = "Cleanliness"),
                ),
            )
        )
}


@XentlyThemePreview
@Composable
private fun ReviewCategoryListSectionPreview(
    @PreviewParameter(ReviewCategoryListSectionPreviewParameterProvider::class)
    response: ReviewCategoriesResponse,
) {
    XentlyTheme {
        ReviewCategoryListSection(
            response = response,
            selectedCategory = null,
            onClickRetry = {},
            onClickSelectCategory = {},
            onClickMoreCategoryOptions = {},
            onClickAddNewReviewCategory = {},
        )
    }
}