package co.ke.xently.libraries.data.core

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

@Immutable
sealed class UiText {
    data class DynamicString(val value: String) : UiText()
    class StringResource(
        @StringRes val id: Int,
        val args: Array<Any> = arrayOf(),
    ) : UiText()
}