package co.ke.xently.libraries.data.core.domain.error

import androidx.compose.runtime.Immutable
import co.ke.xently.libraries.data.core.UiText

@Immutable
interface UiTextError {
    suspend fun toUiText(): UiText
}