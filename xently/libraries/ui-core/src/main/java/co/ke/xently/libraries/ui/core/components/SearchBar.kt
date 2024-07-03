package co.ke.xently.libraries.ui.core.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
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
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    blankQueryIcon: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
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
        content = content,
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
                                Icons.AutoMirrored.Filled.ArrowBack,
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
                    val isQueryBlank by remember(query) {
                        derivedStateOf { query.isBlank() }
                    }
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
                                Icons.Default.Close,
                                contentDescription = stringResource(R.string.content_desc_clear_search_query),
                            )
                        }
                    }
                },
            )
        },
    ) /*{
        Column(Modifier.verticalScroll(rememberScrollState())) {
            repeat(4) { idx ->
                val resultText = "Suggestion $idx"
                ListItem(
                    headlineContent = { Text(text = resultText) },
                    supportingContent = { Text(text = "Additional info") },
                    leadingContent = {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = null
                        )
                    },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                    modifier = Modifier
                        .clickable {
                            onSearch(resultText)
                            expanded = false
                        }
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                )
            }
        }
    }*/
}