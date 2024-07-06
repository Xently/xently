package co.ke.xently.features.reviews.domain

import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

typealias IsDark = Boolean

enum class StarRating(val tint: @Composable (IsDark) -> Color) {
    Full(
        {
            ProgressIndicatorDefaults.linearColor
        },
    ),
    Half(
        {
            ProgressIndicatorDefaults.linearColor
        },
    ),
    Empty(
        {
            ProgressIndicatorDefaults.linearTrackColor
        },
    ),
}