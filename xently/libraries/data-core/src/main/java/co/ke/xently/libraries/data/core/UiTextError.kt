package co.ke.xently.libraries.data.core

import androidx.compose.runtime.Immutable

@Immutable
interface UiTextError {
    suspend fun toUiText(): UiText
}