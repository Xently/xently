package co.ke.xently.features.ui.core.presentation.components

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import co.ke.xently.features.ui.core.presentation.LocalScrollToTheTop
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

@Composable
@OptIn(ExperimentalStdlibApi::class)
fun ScrollToTheTopEffectIfNecessary(state: LazyListState, scroll: Boolean = false) {
    if (LocalScrollToTheTop.current || scroll) {
        LaunchedEffect(Unit) {
            withContext(coroutineContext[CoroutineDispatcher]!! + NonCancellable) {
                state.animateScrollToItem(0)
            }
        }
    }
}

@Composable
@OptIn(ExperimentalStdlibApi::class)
fun ScrollToTheTopEffectIfNecessary(state: LazyGridState, scroll: Boolean = false) {
    if (LocalScrollToTheTop.current || scroll) {
        LaunchedEffect(Unit) {
            withContext(coroutineContext[CoroutineDispatcher]!! + NonCancellable) {
                state.animateScrollToItem(0)
            }
        }
    }
}