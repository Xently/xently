package com.kwanzatukule.features.route.presentation.entry

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.kwanzatukule.features.core.presentation.KwanzaPreview
import com.kwanzatukule.features.core.presentation.theme.KwanzaTukuleTheme
import com.kwanzatukule.libraries.data.route.domain.error.DataError

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteEntryScreen(component: RouteEntryComponent, modifier: Modifier) {
    val state by component.uiState.subscribeAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        component.event.collect {
            when (it) {
                is RouteEntryEvent.Error -> {
                    val result = snackbarHostState.showSnackbar(
                        it.error.asString(context = context),
                        duration = SnackbarDuration.Long,
                        actionLabel = if (it.type is DataError.Network) "Retry" else null,
                    )

                    when (result) {
                        SnackbarResult.Dismissed -> {

                        }

                        SnackbarResult.ActionPerformed -> {

                        }
                    }
                }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = "Route Entry") },
                navigationIcon = {
                    IconButton(onClick = component::handleBackPress) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back",
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AnimatedVisibility(state.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                TextField(
                    value = state.name,
                    onValueChange = component::setName,
                    label = { Text("Name") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Text,
                        capitalization = KeyboardCapitalization.Words,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                )
                TextField(
                    value = state.description,
                    onValueChange = component::setDescription,
                    label = { Text("Description") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Text,
                        capitalization = KeyboardCapitalization.Sentences,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    maxLines = 4,
                    minLines = 4,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(
                        enabled = state.enableSaveButton,
                        modifier = Modifier.weight(1f),
                        onClick = component::onClickSave,
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                    ) { Text(text = "Save") }
                    Button(
                        enabled = state.enableSaveButton,
                        onClick = component::onClickSaveAndAddAnother,
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                    ) { Text(text = "Save & Add Another") }
                }
            }
        }
    }
}

private class RouteEntryUiStateParameterProvider : PreviewParameterProvider<RouteEntryUiState> {
    override val values: Sequence<RouteEntryUiState>
        get() {
            return sequenceOf(
                RouteEntryUiState(),
                RouteEntryUiState(isLoading = true),
                RouteEntryUiState(
                    name = "Kibera",
                    description = "Description about Kibera route",
                ),
                RouteEntryUiState(
                    isLoading = true,
                    name = "Kibera",
                    description = "Description about Kibera route",
                ),
            )
        }
}

@KwanzaPreview
@Composable
private fun RouteEntryScreenPreview(
    @PreviewParameter(RouteEntryUiStateParameterProvider::class)
    uiState: RouteEntryUiState,
) {
    KwanzaTukuleTheme {
        RouteEntryScreen(
            component = RouteEntryComponent.Fake(uiState),
            modifier = Modifier.fillMaxSize(),
        )
    }
}