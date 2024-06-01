package co.ke.xently.features.ui.core.presentation.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyPreview

@Composable
fun OutlinedSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClearClick: () -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
            )
        },
        placeholder = {
            Text(text = placeholder)
        },
        trailingIcon = if (value.isBlank()) null else {
            {
                IconButton(
                    onClick = { onClearClick(); focusManager.clearFocus() },
                    content = {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Clear search query",
                        )
                    },
                )
            }
        },
        shape = RoundedCornerShape(25),
        modifier = modifier,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Search,
            autoCorrectEnabled = false,
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch()
                focusManager.clearFocus()
            },
        ),
    )
}

private class OutlinedSearchFieldStateParameterProvider : PreviewParameterProvider<String> {
    override val values: Sequence<String>
        get() = sequenceOf("", "Example")
}

@XentlyPreview
@Composable
private fun OutlinedSearchFieldPreview(
    @PreviewParameter(OutlinedSearchFieldStateParameterProvider::class)
    value: String,
) {
    XentlyTheme {
        OutlinedSearchField(
            value = value,
            onValueChange = {},
            onSearch = {},
            onClearClick = {},
            placeholder = "Search",
        )
    }
}