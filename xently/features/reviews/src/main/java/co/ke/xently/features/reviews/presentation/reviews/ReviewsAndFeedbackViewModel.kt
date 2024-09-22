package co.ke.xently.features.reviews.presentation.reviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ke.xently.features.reviewcategory.data.source.ReviewCategoryRepository
import co.ke.xently.features.reviews.R
import co.ke.xently.features.reviews.data.domain.error.Result
import co.ke.xently.features.reviews.data.source.ReviewRepository
import co.ke.xently.features.reviews.presentation.reviews.ReviewsAndFeedbackAction.FetchReviewCategories
import co.ke.xently.features.reviews.presentation.reviews.ReviewsAndFeedbackAction.FetchShopReviewSummary
import co.ke.xently.features.reviews.presentation.reviews.ReviewsAndFeedbackAction.FetchStoreReviewSummary
import co.ke.xently.features.reviews.presentation.reviews.ReviewsAndFeedbackAction.FetchStoreStatistics
import co.ke.xently.features.reviews.presentation.reviews.ReviewsAndFeedbackAction.RemoveSelectedMonth
import co.ke.xently.features.reviews.presentation.reviews.ReviewsAndFeedbackAction.RemoveSelectedYear
import co.ke.xently.features.reviews.presentation.reviews.ReviewsAndFeedbackAction.SelectMonth
import co.ke.xently.features.reviews.presentation.reviews.ReviewsAndFeedbackAction.SelectReviewCategory
import co.ke.xently.features.reviews.presentation.reviews.ReviewsAndFeedbackAction.SelectYear
import co.ke.xently.features.reviews.presentation.theme.STAR_RATING_COLOURS
import co.ke.xently.libraries.ui.core.domain.coolFormat
import com.aay.compose.barChart.model.BarParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import co.ke.xently.features.reviewcategory.data.domain.error.Result as ReviewCategoryResult

@HiltViewModel
internal class ReviewsAndFeedbackViewModel @Inject constructor(
    private val repository: ReviewRepository,
    private val reviewCategoryRepository: ReviewCategoryRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReviewsAndFeedbackUiState())
    val uiState: StateFlow<ReviewsAndFeedbackUiState> = _uiState.asStateFlow()

    private val _event = Channel<ReviewsAndFeedbackEvent>()
    val event: Flow<ReviewsAndFeedbackEvent> = _event.receiveAsFlow()

    init {
        onAction(FetchReviewCategories)
        onAction(FetchShopReviewSummary)
        onAction(FetchStoreReviewSummary)
    }

    fun onAction(action: ReviewsAndFeedbackAction) {
        when (action) {
            is ReviewsAndFeedbackAction.Refresh -> {
                onAction(FetchReviewCategories)
                onAction(FetchShopReviewSummary)
                onAction(FetchStoreReviewSummary)
                onAction(FetchStoreStatistics(action.context))
            }

            is SelectReviewCategory -> {
                if (action.category != _uiState.value.selectedCategory) {
                    _uiState.update {
                        it.copy(selectedCategory = action.category)
                    }
                    onAction(FetchStoreStatistics(action.context))
                }
            }

            is SelectYear -> {
                _uiState.update { state ->
                    val selectedFilters = state.selectedFilters.copy(
                        year = action.year,
                    )
                    state.copy(selectedFilters = selectedFilters)
                }
            }

            is RemoveSelectedYear -> {
                _uiState.update { state ->
                    val selectedFilters = state.selectedFilters.copy(
                        year = null,
                        month = null,
                    )
                    state.copy(selectedFilters = selectedFilters)
                }
            }

            is SelectMonth -> {
                _uiState.update { state ->
                    val selectedFilters = state.selectedFilters.copy(
                        month = action.month,
                    )
                    state.copy(selectedFilters = selectedFilters)
                }
            }

            is RemoveSelectedMonth -> {
                _uiState.update { state ->
                    val selectedFilters = state.selectedFilters.copy(
                        month = null,
                    )
                    state.copy(selectedFilters = selectedFilters)
                }
            }

            is FetchStoreStatistics -> {
                val category = _uiState.value.selectedCategory ?: return
                val filters = _uiState.value.selectedFilters

                viewModelScope.launch {
                    repository.findStoreReviewStatistics(category = category, filters = filters)
                        .onStart { _uiState.update { it.copy(statisticsResponse = StatisticsResponse.Loading) } }
                        .collect { result ->
                            val response = when (result) {
                                is Result.Failure -> StatisticsResponse.Failure(
                                    error = result.error.toUiText(),
                                    type = result.error,
                                )

                                is Result.Success -> {
                                    val statistics = result.data

                                    /*while (statistics.groupedStatistics.isEmpty()) {
                                        // Avoid showing the user empty state, instead fallback
                                        // to previous the most recent month, or remove the month
                                        // selection completely.
                                        val month = _uiState.value.selectedFilters.month ?: break
                                        if (month.ordinal == 0) {
                                            onAction(RemoveSelectedMonth)
                                        } else {
                                            onAction(SelectMonth(Month(month.ordinal)))
                                        }
                                        delay(200)
                                        onAction(FetchStoreStatistics(action.context))
                                    }*/

                                    val groups = statistics.groupedStatistics.map {
                                        it.group
                                    }.distinct()
                                    val barParameters = statistics.groupedStatistics.groupBy {
                                        it.starRating
                                    }.mapValues { (_, stats) ->
                                        groups.map { group ->
                                            stats.firstOrNull { it.group == group }
                                                ?.count
                                                ?.toDouble()
                                                ?: 0.0
                                        }
                                    }.map { (star, counts) ->
                                        BarParameters(
                                            dataName = action.context.getString(
                                                R.string.chart_legend_star,
                                                star,
                                                counts.sum().coolFormat(),
                                            ),
                                            data = counts,
                                            barColor = STAR_RATING_COLOURS[star]!!,
                                        )
                                    }
                                    StatisticsResponse.Success(
                                        data = statistics,
                                        barGraphData = BarGraphData(
                                            xAxis = groups,
                                            barParameters = barParameters,
                                        ),
                                    )
                                }
                            }

                            _uiState.update {
                                it.copy(statisticsResponse = response)
                            }
                        }
                }
            }

            FetchStoreReviewSummary -> {
                viewModelScope.launch {
                    repository.findSummaryReviewForCurrentlyActiveStore()
                        .onStart { _uiState.update { it.copy(storeReviewSummaryResponse = ReviewSummaryResponse.Loading) } }
                        .collect { result ->
                            val response = when (result) {
                                is Result.Failure -> ReviewSummaryResponse.Failure(
                                    error = result.error.toUiText(),
                                    type = result.error,
                                )

                                is Result.Success -> ReviewSummaryResponse.Success(data = result.data)
                            }

                            _uiState.update {
                                it.copy(storeReviewSummaryResponse = response)
                            }
                        }
                }
            }

            FetchShopReviewSummary -> {
                viewModelScope.launch {
                    repository.findSummaryReviewForCurrentlyActiveShop()
                        .onStart { _uiState.update { it.copy(shopReviewSummaryResponse = ReviewSummaryResponse.Loading) } }
                        .collect { result ->
                            val response = when (result) {
                                is Result.Failure -> ReviewSummaryResponse.Failure(
                                    error = result.error.toUiText(),
                                    type = result.error,
                                )

                                is Result.Success -> ReviewSummaryResponse.Success(data = result.data)
                            }

                            _uiState.update {
                                it.copy(shopReviewSummaryResponse = response)
                            }
                        }
                }
            }

            FetchReviewCategories -> {
                viewModelScope.launch {
                    reviewCategoryRepository.findAllReviewCategories()
                        .onStart { _uiState.update { it.copy(categoriesResponse = ReviewCategoriesResponse.Loading) } }
                        .collect { result ->
                            val response = when (result) {
                                is ReviewCategoryResult.Failure -> ReviewCategoriesResponse.Failure(
                                    error = result.error.toUiText(),
                                    type = result.error,
                                )

                                is ReviewCategoryResult.Success -> {
                                    if (result.data.isEmpty()) {
                                        ReviewCategoriesResponse.Success.Empty
                                    } else {
                                        ReviewCategoriesResponse.Success.NonEmpty(data = result.data)
                                    }
                                }
                            }

                            _uiState.update {
                                it.copy(categoriesResponse = response)
                            }
                        }
                }
            }
        }
    }
}