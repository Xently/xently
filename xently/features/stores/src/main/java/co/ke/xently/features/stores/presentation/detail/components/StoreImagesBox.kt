package co.ke.xently.features.stores.presentation.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.data.core.Link
import co.ke.xently.libraries.data.image.domain.UploadResponse
import co.ke.xently.libraries.ui.core.XentlyThemePreview
import co.ke.xently.libraries.ui.image.XentlyImage
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue
import kotlin.time.Duration.Companion.seconds


private fun Modifier.animatePageChanges(
    pagerState: PagerState,
    index: Int,
) = graphicsLayer {
    val x = (pagerState.currentPage - index + pagerState.currentPageOffsetFraction) * 2
    alpha = 1f - (x.absoluteValue * 0.7f).coerceIn(0f, 0.7f)
    val scale = 1f - (x.absoluteValue * 0.4f).coerceIn(0f, 0.4f)
    scaleX = scale
    scaleY = scale
    rotationY = x * 15f
}

@Composable
internal fun StoreImagesBox(modifier: Modifier = Modifier, images: List<UploadResponse>) {
    val pagerState = rememberPagerState(
        initialPageOffsetFraction = 0f,
        pageCount = { images.size },
    )

    LaunchedEffect(pagerState, images) {
        while (images.isNotEmpty()) {
            delay(10.seconds)
            pagerState.animateScrollToPage(
                (pagerState.currentPage + 1).mod(images.size)
            )
        }
    }

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier
            .fillMaxWidth()
            .height(270.dp)
            .then(modifier),
    ) {
        HorizontalPager(
            state = pagerState,
            key = { images[it].url() },
            beyondViewportPageCount = 1,
        ) { index ->
            XentlyImage(
                data = images[index],
                modifier = Modifier
                    .fillMaxSize()
                    .animatePageChanges(pagerState, index),
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 20.dp),
        ) {
            for (index in images.indices) {
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
}

@XentlyThemePreview
@Composable
private fun StoreImagesBoxPreview() {
    XentlyTheme {
        StoreImagesBox(
            images = remember {
                List(5) {
                    UploadResponse(links = mapOf("media" to Link(href = "https://www.google.com${it + 1}")))
                }
            },
        )
    }
}