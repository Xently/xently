package co.ke.xently.features.settings.domain

import androidx.compose.runtime.Stable

@Stable
data class Setting(
    val title: String,
    val subtitle: String?,
    val onClick: () -> Unit = {},
)