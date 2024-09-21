package co.ke.xently.features.reviews.presentation.comments

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import co.ke.xently.features.reviewcategory.data.domain.error.Result
import co.ke.xently.features.reviewcategory.data.source.ReviewCategoryRepository
import co.ke.xently.features.reviews.data.domain.Review
import co.ke.xently.features.reviews.data.domain.ReviewFilters
import co.ke.xently.features.reviews.data.domain.error.ReviewCategoryNotFoundException
import co.ke.xently.features.reviews.data.source.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@HiltViewModel
internal class ReviewCommentListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: ReviewRepository,
    private val categoryRepository: ReviewCategoryRepository,
) : ViewModel() {
    private companion object {
        private val KEY = ReviewCommentListViewModel::class.java.name.plus("SELECTED_STARS")
    }

    private val _uiState = MutableStateFlow(ReviewCommentListUiState())
    val uiState: StateFlow<ReviewCommentListUiState> = _uiState.asStateFlow()

    private val _event = Channel<ReviewCommentListEvent>()
    val event: Flow<ReviewCommentListEvent> = _event.receiveAsFlow()

    private val _selectedStar = savedStateHandle.getStateFlow<Int?>(KEY, null)

    @OptIn(ExperimentalCoroutinesApi::class)
    val reviews: Flow<PagingData<Review>> =
        savedStateHandle.getStateFlow<String?>("categoryName", null)
            .filterNotNull()
            .flatMapLatest(categoryRepository::findCategoryByName)
            .combineTransform(_selectedStar) { result, selectedStar ->
                emitAll(
                    when (result) {
                        is Result.Failure -> throw ReviewCategoryNotFoundException()
                        is Result.Success -> {
                            repository.getReviews(
                                url = result.data.links["reviews"]!!.hrefWithoutQueryParamTemplates(),
                                filters = ReviewFilters(starRating = selectedStar),
                            )
                        }
                    }
                )
            }.cachedIn(viewModelScope)

    fun onAction(action: ReviewCommentListAction) {
        when (action) {
            is ReviewCommentListAction.ChangeQuery -> {
                _uiState.update { it.copy(query = action.query) }
            }

            is ReviewCommentListAction.SelectStarRating -> {
                savedStateHandle[KEY] = action.star.number
                _uiState.update { state ->
                    state.copy(
                        stars = state.stars.map {
                            it.copy(selected = it.number == action.star.number)
                        },
                    )
                }
            }

            is ReviewCommentListAction.RemoveStarRating -> {
                savedStateHandle[KEY] = null
                _uiState.update { state ->
                    state.copy(
                        stars = state.stars.map {
                            it.copy(selected = false)
                        },
                    )
                }
            }

            is ReviewCommentListAction.Search -> {
//                _filters.update { it.copy(query = action.query) }
            }
        }
    }
}