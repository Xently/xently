package co.ke.xently.features.ui.core.presentation.components


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.dokar.chiptextfield.Chip
import com.dokar.chiptextfield.m3.OutlinedChipTextField
import com.dokar.chiptextfield.rememberChipTextFieldState


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun XentlyOutlinedChipTextField(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    chips: List<Chip>,
    onSubmit: (String) -> Unit,
    onTextChange: (String) -> Unit = {},
    retrieveSuggestions: @Composable () -> List<String> = { emptyList() },
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    onClickTrailingIcon: (Chip) -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    val state = rememberChipTextFieldState(chips = chips)

    LaunchedEffect(state, chips) {
        state.chips = chips
    }

    var value by rememberSaveable { mutableStateOf("") }

    Column {
        val suggestions = retrieveSuggestions()

        var expand by rememberSaveable(suggestions, value) {
            mutableStateOf(suggestions.isNotEmpty() && value.isNotBlank())
        }

        val doSubmit: (String) -> Unit = {
            onSubmit(it)
            expand = false
            value = ""
        }

        AnimatedVisibility(expand) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(suggestions) { suggestion ->
                    SuggestionChip(
                        label = { Text(text = suggestion) },
                        onClick = { doSubmit(suggestion) },
                    )
                }
            }
        }

        OutlinedChipTextField(
            shape = CardDefaults.shape,
            state = state,
            enabled = enabled,
            value = value,
            modifier = modifier,
            label = label,
            placeholder = placeholder,
            chipVerticalSpacing = 8.dp,
            chipHorizontalSpacing = 8.dp,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            chipTrailingIcon = { CloseButton { onClickTrailingIcon(it) } },
            onValueChange = {
                var text = it.trimEnd()
                value = if (!text.endsWith(",")) {
                    onTextChange(it)
                    it
                } else {
                    text = text.replace("\\s*,\\s*$".toRegex(), "").trimStart()
                    if (text.isNotBlank()) {
                        doSubmit(text)
                    }
                    ""
                }
            },
            onSubmit = {
                val text = it.trim()
                if (text.isBlank()) {
                    focusManager.clearFocus()
                } else {
                    doSubmit(text)
                }
                null
            },
        )
    }
}


@Composable
private fun CloseButton(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Black.copy(alpha = 0.3f),
    strokeColor: Color = Color.White,
    startPadding: Dp = 0.dp,
    endPadding: Dp = 6.dp,
    onClick: () -> Unit,
) {
    Row(modifier = modifier.padding(start = startPadding, end = endPadding)) {
        val padding = with(LocalDensity.current) { 6.dp.toPx() }
        val strokeWidth = with(LocalDensity.current) { 1.2.dp.toPx() }
        val viewConfiguration = LocalViewConfiguration.current
        val viewConfigurationOverride = remember(viewConfiguration) {
            ViewConfigurationOverride(
                base = viewConfiguration,
                minimumTouchTargetSize = DpSize(24.dp, 24.dp)
            )
        }
        CompositionLocalProvider(LocalViewConfiguration provides viewConfigurationOverride) {
            Canvas(
                modifier = modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(backgroundColor)
                    .clickable(onClick = onClick)
            ) {
                drawLine(
                    color = strokeColor,
                    start = Offset(padding, padding),
                    end = Offset(size.width - padding, size.height - padding),
                    strokeWidth = strokeWidth
                )
                drawLine(
                    color = strokeColor,
                    start = Offset(padding, size.height - padding),
                    end = Offset(size.width - padding, padding),
                    strokeWidth = strokeWidth
                )
            }
        }
    }
}

internal class ViewConfigurationOverride(
    base: ViewConfiguration,
    override val doubleTapMinTimeMillis: Long = base.doubleTapMinTimeMillis,
    override val doubleTapTimeoutMillis: Long = base.doubleTapTimeoutMillis,
    override val longPressTimeoutMillis: Long = base.longPressTimeoutMillis,
    override val touchSlop: Float = base.touchSlop,
    override val minimumTouchTargetSize: DpSize = base.minimumTouchTargetSize,
) : ViewConfiguration
