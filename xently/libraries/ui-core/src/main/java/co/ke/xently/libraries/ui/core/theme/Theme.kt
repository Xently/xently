package co.ke.xently.libraries.ui.core.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import co.ke.xently.libraries.ui.core.components.ThemeSetting
import co.ke.xently.libraries.ui.core.components.isDarkState


typealias IsDark = Boolean

@Composable
fun AppTheme(
    /** Dynamic color is available on Android 12+ **/
    dynamicColor: Boolean,
    colorScheme: @Composable (IsDark) -> ColorScheme,
    shapes: Shapes = MaterialTheme.shapes,
    typography: Typography = MaterialTheme.typography,
    setting: ThemeSetting = rememberSaveable { ThemeSetting.SystemDefault },
    content: @Composable () -> Unit,
) {
    val isDarkMode by setting.isDarkState()
    val isDarkState = rememberSaveable(isDarkMode) {
        mutableStateOf(isDarkMode)
    }
    CompositionLocalProvider(LocalThemeIsDark provides isDarkState) {
        val isDark by isDarkState
        val color = colorScheme(isDark)
        SystemAppearance(
            isLightTheme = !isDark,
            dynamicColor = dynamicColor,
            scheme = color,
        )
        MaterialTheme(
            shapes = shapes,
            typography = typography,
            content = content,
            colorScheme = color,
        )
    }
}

@Composable
private fun SystemAppearance(isLightTheme: Boolean, dynamicColor: Boolean, scheme: ColorScheme) {
    val colorScheme = if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val context = LocalContext.current
        if (isLightTheme) {
            dynamicLightColorScheme(context)
        } else {
            dynamicDarkColorScheme(context)
        }
    } else scheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                !isLightTheme
        }
    }

    val systemBarColor = android.graphics.Color.TRANSPARENT
    LaunchedEffect(isLightTheme) {
        val window = (view.context as Activity).window
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = systemBarColor
        window.navigationBarColor = systemBarColor
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = isLightTheme
            isAppearanceLightNavigationBars = isLightTheme
        }
    }
}
