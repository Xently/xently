package co.ke.xently.libraries.ui.core.components

import androidx.annotation.StringRes
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.setValue
import co.ke.xently.libraries.ui.core.R
import co.ke.xently.libraries.ui.core.theme.LocalThemeIsDark

enum class ThemeSetting(@StringRes val label: Int) {
    SystemDefault(R.string.theme_system_default),
    Light(R.string.theme_light),
    Dark(R.string.theme_dark),
}

@Composable
fun ThemeSetting.isDarkState(): State<Boolean> {
    var isDark by LocalThemeIsDark.current
    val systemIsDark = isSystemInDarkTheme()

    return produceState(systemIsDark, this) {
        isDark = when (this@isDarkState) {
            ThemeSetting.SystemDefault -> systemIsDark
            ThemeSetting.Light -> false
            ThemeSetting.Dark -> true
        }.also { this.value = it }
    }
}