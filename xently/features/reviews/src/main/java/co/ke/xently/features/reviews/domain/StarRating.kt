package co.ke.xently.features.reviews.domain

import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import co.ke.xently.features.reviews.presentation.theme.xentlyLinearProgressLight
import co.ke.xently.features.reviews.presentation.theme.xentlyLinearProgressLightTrack

typealias IsDark = Boolean

enum class StarRating(val tint: @Composable (IsDark) -> Color) {
    Full(
        {
            if (it) {
                ProgressIndicatorDefaults.linearColor
            } else {
                xentlyLinearProgressLight
            }
        },
    ),
    Half(
        {
            if (it) {
                ProgressIndicatorDefaults.linearColor
            } else {
                xentlyLinearProgressLight
            }
        },
    ),
    Empty(
        {
            if (it) {
                ProgressIndicatorDefaults.linearTrackColor
            } else {
                xentlyLinearProgressLightTrack
            }
        },
    ),
}