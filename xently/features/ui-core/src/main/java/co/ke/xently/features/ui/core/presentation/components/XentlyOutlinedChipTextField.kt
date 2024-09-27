package co.ke.xently.features.ui.core.presentation.components


import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import com.dokar.chiptextfield.Chip
import com.dokar.chiptextfield.m3.OutlinedChipTextField
import com.dokar.chiptextfield.rememberChipTextFieldState

@Composable
fun XentlyOutlinedChipTextField(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    chips: List<Chip>,
    onSubmit: (String) -> Unit,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
) {
    val focusManager = LocalFocusManager.current
    val chipState = rememberChipTextFieldState(chips = chips)

    LaunchedEffect(chipState, chips) {
        chipState.chips = chips
    }

    var value by remember { mutableStateOf(TextFieldValue()) }

    OutlinedChipTextField(
        shape = CardDefaults.shape,
        state = chipState,
        enabled = enabled,
        value = value,
        modifier = modifier,
        label = label,
        placeholder = placeholder,
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        onValueChange = {
            var text = it.text.trimEnd()
            value = if (!text.endsWith(",")) it else {
                text = text.replace("\\s*,\\s*$".toRegex(), "").trimStart()
                if (text.isNotBlank()) {
                    onSubmit(text)
                    chipState.addChip(Chip(text))
                }
                TextFieldValue()
            }
        },
        onSubmit = {
            val text = it.text.trim()
            if (text.isBlank()) {
                focusManager.clearFocus()
            } else {
                onSubmit(text)
            }
            Chip(text)
        },
    )
}