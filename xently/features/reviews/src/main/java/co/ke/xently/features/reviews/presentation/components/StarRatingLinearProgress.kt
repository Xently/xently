package co.ke.xently.features.reviews.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import co.ke.xently.features.reviews.presentation.theme.xentlyLinearProgressLight
import co.ke.xently.features.reviews.presentation.theme.xentlyLinearProgressLightTrack
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyThemePreview

@Composable
internal fun StarRatingLinearProgress(
    progress: Float,
    isDark: Boolean,
    modifier: Modifier = Modifier,
) {
    val animatedProgress by animateFloatAsState(
        progress,
        label = "progress-animation",
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
    )
    val color = /*if (isDark) */ProgressIndicatorDefaults.linearColor
//    else xentlyLinearProgressLight
    val trackColor = /*if (isDark) */ProgressIndicatorDefaults.linearTrackColor
//    else xentlyLinearProgressLightTrack
    Box(
        modifier = modifier
            .height(12.dp)
            .background(trackColor),
    ) {
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.matchParentSize(),
            color = color,
            trackColor = trackColor,
            strokeCap = StrokeCap.Square,
            gapSize = 0.dp,
            drawStopIndicator = {},
        )
    }
}

private data class StarRatingLinearProgressState(
    val progress: Float,
    val isDark: Boolean,
)

private class StarRatingLinearProgressStateProvider :
    PreviewParameterProvider<StarRatingLinearProgressState> {
    override val values = sequenceOf(
        StarRatingLinearProgressState(
            progress = 0f,
            isDark = true,
        ),
        StarRatingLinearProgressState(
            progress = 0f,
            isDark = false,
        ),
        StarRatingLinearProgressState(
            progress = 0.5f,
            isDark = true,
        ),
        StarRatingLinearProgressState(
            progress = 0.5f,
            isDark = false,
        ),
        StarRatingLinearProgressState(
            progress = 1f,
            isDark = true,
        ),
        StarRatingLinearProgressState(
            progress = 1f,
            isDark = false,
        ),
        StarRatingLinearProgressState(
            progress = 3f,
            isDark = true,
        ),
        StarRatingLinearProgressState(
            progress = 3f,
            isDark = false,
        ),
        StarRatingLinearProgressState(
            progress = 5f,
            isDark = true,
        ),
        StarRatingLinearProgressState(
            progress = 5f,
            isDark = false,
        ),
    )
}

@XentlyThemePreview
@Composable
private fun StarRatingLinearProgressPreview(
    @PreviewParameter(StarRatingLinearProgressStateProvider::class)
    state: StarRatingLinearProgressState,
) {
    XentlyTheme {
        StarRatingLinearProgress(
            progress = state.progress,
            isDark = state.isDark,
            modifier = Modifier
                .padding(8.dp)
                .width(200.dp),
        )
    }
}