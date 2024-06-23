package co.ke.xently.features.ui.core.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import co.ke.xently.features.ui.core.R
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.ui.core.XentlyThemePreview

@Composable
fun AddCategorySection(
    name: String,
    modifier: Modifier = Modifier,
    shape: Shape = CardDefaults.shape,
    disableInteractions: Boolean = false,
    onNameValueChange: (String) -> Unit,
    onAddClick: () -> Unit,
) {
    Card(
        shape = shape,
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = .07f),
        ),
    ) {
        Text(
            text = stringResource(R.string.headline_enter_category),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val focusManager = LocalFocusManager.current
            OutlinedTextField(
                value = name,
                enabled = !disableInteractions,
                onValueChange = onNameValueChange,
                placeholder = {
                    Text(
                        text = stringResource(R.string.text_field_placeholder_category_name),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                modifier = Modifier.weight(1f),
                maxLines = 1,
                singleLine = true,
                shape = CardDefaults.shape,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.onSurface,
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.Sentences,
                ),
                keyboardActions = KeyboardActions { focusManager.clearFocus() },
            )
            val enableButton by remember(disableInteractions, name) {
                derivedStateOf {
                    !disableInteractions
                            && name.isNotBlank()
                }
            }
            Button(
                enabled = enableButton,
                onClick = onAddClick,
                content = {
                    Text(text = stringResource(R.string.action_add).toUpperCase(Locale.current))
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White,
                ),
                shape = CardDefaults.shape,
                modifier = Modifier.height(56.dp),
            )
        }
    }
}

private data class AddCategorySectionUiState(
    val name: String = "",
    val disableInteractions: Boolean = false,
)

private class AddCategorySectionUiStateParameterProvider :
    PreviewParameterProvider<AddCategorySectionUiState> {
    override val values: Sequence<AddCategorySectionUiState>
        get() = sequenceOf(
            AddCategorySectionUiState(),
            AddCategorySectionUiState(name = "Example category"),
            AddCategorySectionUiState(disableInteractions = true),
            AddCategorySectionUiState(name = "Example category", disableInteractions = true),
        )
}

@XentlyThemePreview
@Composable
private fun AddCategorySectionPreview(
    @PreviewParameter(AddCategorySectionUiStateParameterProvider::class)
    state: AddCategorySectionUiState,
) {
    XentlyTheme {
        AddCategorySection(
            modifier = Modifier.padding(16.dp),
            name = state.name,
            disableInteractions = state.disableInteractions,
            onAddClick = { },
            onNameValueChange = {},
        )
    }
}