package co.ke.xently.libraries.ui.pagination

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.produceState
import androidx.paging.LoadState
import co.ke.xently.libraries.data.core.domain.error.UiTextError
import co.ke.xently.libraries.ui.core.LocalDispatchersProvider
import kotlinx.coroutines.async

@Immutable
sealed interface ListState {
    data object Empty : ListState
    data object Loading : ListState
    data object Ready : ListState
    class Error(val error: UiTextError) : ListState
}

@Composable
fun <E : UiTextError> LoadState.asListState(
    itemCount: Int,
    errorMapper: suspend (Throwable) -> E,
): ListState {
    val dispatchersProvider = LocalDispatchersProvider.current
    return produceState<ListState>(ListState.Loading, this, itemCount, endOfPaginationReached) {
        value = when {
            itemCount == 0 && this@asListState is LoadState.Error -> {
                val uiText = async(dispatchersProvider.default) {
                    errorMapper(this@asListState.error)
                }
                ListState.Error(uiText.await())
            }

            itemCount == 0 && this@asListState is LoadState.NotLoading -> {
                if (this@asListState.endOfPaginationReached) {
                    ListState.Empty
                } else value
            }

            itemCount == 0 && this@asListState is LoadState.Loading -> ListState.Loading
            else -> ListState.Ready
        }
    }.value
}