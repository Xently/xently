package co.ke.xently.libraries.ui.core.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import co.ke.xently.libraries.ui.core.R


@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SearchBar(
    query: String,
    placeholder: String,
    modifier: Modifier = Modifier,
    bottomPadding: Dp? = null,
    horizontalPadding: Dp? = null,
    exitSearchIcon: ImageVector? = null,
    clearSearchQueryIcon: ImageVector? = null,
    suggestions: List<String> = emptyList(),
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    blankQueryIcon: @Composable (() -> Unit)? = null,
) {
    val isQueryBlank by remember(query) { derivedStateOf { query.isBlank() } }
    var expanded by rememberSaveable { mutableStateOf(false) }
    SearchBar(
        modifier = Modifier
            .run {
                if (expanded) {
                    Modifier
                } else {
                    fillMaxWidth()
                        .padding(bottom = bottomPadding ?: 16.dp)
                        .padding(horizontal = horizontalPadding ?: 16.dp)
                }
            }
            .then(modifier),
        expanded = expanded,
        onExpandedChange = { expanded = it },
        inputField = {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = { expanded = false; onSearch(it) },
                expanded = expanded,
                onExpandedChange = { expanded = it },
                placeholder = {
                    Text(
                        text = placeholder,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                leadingIcon = {
                    IconButton(onClick = { expanded = !expanded }) {
                        if (expanded) {
                            Icon(
                                exitSearchIcon
                                    ?: Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.content_desc_exit_search),
                            )
                        } else {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = placeholder,
                            )
                        }
                    }
                },
                trailingIcon = {
                    if (isQueryBlank) {
                        blankQueryIcon?.invoke()
                    } else {
                        IconButton(
                            onClick = {
                                onQueryChange("")
                                onSearch("")
                                expanded = false
                            },
                        ) {
                            Icon(
                                clearSearchQueryIcon
                                    ?: Icons.Default.Clear,
                                contentDescription = stringResource(R.string.content_desc_clear_search_query),
                            )
                        }
                    }
                },
            )
        },
    ) {
        Column(Modifier.verticalScroll(rememberScrollState())) {
            if (!isQueryBlank) {
                SuggestionListItem(suggestion = query) {
                    onSearch(query)
                    expanded = false
                }
            }
            suggestions.forEach { suggestion ->
                SuggestionListItem(suggestion = suggestion) {
                    onSearch(suggestion)
                    expanded = false
                }
            }
        }
    }
}

@Composable
private fun SuggestionListItem(
    suggestion: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    ListItem(
        headlineContent = {
            Text(
                text = suggestion,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        leadingContent = {
            Icon(
                Icons.Filled.Search,
                contentDescription = null,
            )
        },
        trailingContent = {
            Icon(
                Icons.Default.ArrowOutward,
                contentDescription = null,
            )
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
            .clickable(onClick = onClick)
            /*.padding(horizontal = 16.dp, vertical = 4.dp)*/,
    )
}

@Preview(showBackground = true)
@Composable
private fun SuggestionListItemPreview() {
    SuggestionListItem(suggestion = "Example suggestion") {}
}