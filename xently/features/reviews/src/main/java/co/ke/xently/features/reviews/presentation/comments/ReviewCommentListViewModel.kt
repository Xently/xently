package co.ke.xently.features.reviews.presentation.comments

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import co.ke.xently.features.reviews.data.domain.Review
import co.ke.xently.features.reviews.data.domain.ReviewFilters
import co.ke.xently.features.reviews.data.source.ReviewRepository
import co.ke.xently.libraries.pagination.data.XentlyPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
internal class ReviewCommentListViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val repository: ReviewRepository,
) : ViewModel() {
    private companion object {
        private const val KEY = "co.ke.xently.features.reviews.presentation.edit.SELECTED_STARS"
    }

    private val _uiState = MutableStateFlow(ReviewCommentListUiState())
    val uiState: StateFlow<ReviewCommentListUiState> = _uiState.asStateFlow()

    private val _event = Channel<ReviewCommentListEvent>()
    val event: Flow<ReviewCommentListEvent> = _event.receiveAsFlow()

    private val _selectedStar = savedStateHandle.getStateFlow<Int?>(KEY, null)

    val reviews: Flow<PagingData<Review>> = _selectedStar.flatMapLatest { selectedStar ->
        Pager(
            PagingConfig(
                pageSize = 20,
                initialLoadSize = 20,
            )
        ) {
            XentlyPagingSource { url ->
                repository.getReviews(
                    url = url,
                    filters = ReviewFilters(starRating = selectedStar),
                )
            }
        }.flow
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