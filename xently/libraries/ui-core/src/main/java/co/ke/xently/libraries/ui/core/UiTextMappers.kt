package co.ke.xently.libraries.ui.core

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.produceState
import androidx.compose.ui.platform.LocalContext
import co.ke.xently.libraries.data.core.UiText
import co.ke.xently.libraries.data.core.UiText.DynamicString
import co.ke.xently.libraries.data.core.UiText.StringResource
import co.ke.xently.libraries.data.core.domain.error.UiTextError
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll


@Composable
fun Throwable.toUiTextError(error: suspend (Throwable) -> UiTextError): UiTextError? {
    return produceState<UiTextError?>(null, this) {
        value = error(this@toUiTextError)
    }.value
}

@Composable
fun UiTextError.asString(): String {
    val context = LocalContext.current
    return produceState("", this, context) {
        value = toUiText().asString(context = context)
    }.value
}

@Composable
fun List<UiTextError>.asString(): String {
    val context = LocalContext.current
    return produceState("", this, context) {
        value = map {
            async { it.toUiText().asString(context = context) }
        }.awaitAll().joinToString("\n") { "• $it" }
    }.value
}

@Composable
fun UiText.asString(): String {
    return when (this) {
        is DynamicString -> value
        is StringResource -> LocalContext.current.getString(id, *args)
    }
}

fun UiText.asString(context: Context): String {
    return when (this) {
        is DynamicString -> value
        is StringResource -> context.getString(id, *args)
    }
}