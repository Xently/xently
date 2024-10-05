package co.ke.xently.features.ui.core.presentation.components


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardCapitalization
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
) {
    val focusManager = LocalFocusManager.current
    val chipState = rememberChipTextFieldState(chips = chips)

    LaunchedEffect(chipState, chips) {
        chipState.chips = chips
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
            state = chipState,
            enabled = enabled,
            value = value,
            modifier = modifier,
            label = label,
            placeholder = placeholder,
            chipVerticalSpacing = 8.dp,
            chipHorizontalSpacing = 8.dp,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
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