package co.ke.xently.libraries.ui.core.components

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import com.valentinilk.shimmer.shimmer


fun Modifier.shimmer(shimmer: Boolean, color: Color? = null): Modifier {
    return composed {
        if (shimmer) {
            background(color ?: MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                .shimmer()
        } else Modifier
    }
}