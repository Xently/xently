package com.kwanzatukule.features.customer.presentation.entry

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import co.ke.xently.libraries.location.tracker.presentation.ForegroundLocationTracker
import co.ke.xently.libraries.location.tracker.presentation.rememberLocationPermissionLauncher
import co.ke.xently.libraries.ui.core.XentlyPreview
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.kwanzatukule.features.core.presentation.theme.KwanzaTukuleTheme
import com.kwanzatukule.features.customer.presentation.entry.components.CustomerLocationPickerMap
import com.kwanzatukule.libraries.data.customer.domain.error.DataError
import com.kwanzatukule.libraries.data.route.domain.Route
import com.kwanzatukule.libraries.data.route.domain.RouteSummary
import kotlinx.coroutines.launch
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerEntryScreen(component: CustomerEntryComponent, modifier: Modifier) {
    val state by component.uiState.subscribeAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        component.event.collect {
            when (it) {
                is CustomerEntryEvent.Error -> {
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
                title = { Text(text = "Customer Entry") },
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
                    singleLine = true,
                )
                TextField(
                    value = state.phone,
                    onValueChange = component::setPhone,
                    label = { Text("Phone") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Phone,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    singleLine = true,
                )
                TextField(
                    value = state.email,
                    onValueChange = component::setEmail,
                    label = { Text("Email") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Email,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    singleLine = true,
                )

                var locationPermissionGranted by remember {
                    mutableStateOf(false)
                }

                var shouldTrackLocation by remember {
                    mutableStateOf(false)
                }
                val locationPermissionLauncher = rememberLocationPermissionLauncher {
                    locationPermissionGranted = it
                    shouldTrackLocation = it
                }

                if (shouldTrackLocation && locationPermissionGranted) {
                    ForegroundLocationTracker(onLocationUpdates = component::setLocation)
                }

                OutlinedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    onClick = {
                        if (locationPermissionGranted) {
                            shouldTrackLocation = true
                        } else {
                            scope.launch {
                                locationPermissionLauncher.launch()
                            }
                        }
                    },
                ) { Text(text = "Use my current location") }

                CustomerLocationPickerMap(
                    modifier = Modifier
                        .height(400.dp)
                        .padding(horizontal = 16.dp),
                    location = state.location,
                    positionMarkerAtTheCentre = true,
                    enableMyLocation = locationPermissionGranted,
                    onMarkerPositionChange = component::setLocation,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
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

private class CustomerEntryUiStateParameterProvider :
    PreviewParameterProvider<CustomerEntryUiState> {
    override val values: Sequence<CustomerEntryUiState>
        get() {
            val route = Route(
                name = "Kibera",
                description = "En-route Lang'ata road",
                summary = RouteSummary(
                    bookedOrder = Random.nextInt(100),
                    variance = Random.nextInt(Int.MIN_VALUE, Int.MAX_VALUE),
                    numberOfCustomers = Random.nextInt(100),
                    totalRouteCustomers = Random.nextInt(100),
                    geographicalDistance = Random.nextInt(1_000, 10_000),
                ),
            )
            return sequenceOf(
                CustomerEntryUiState(route = route),
                CustomerEntryUiState(
                    route = route,
                    isLoading = true,
                ),
                CustomerEntryUiState(
                    route = route,
                    name = "Kibera",
                    email = "customer@example.com",
                    phone = "+254712345678",
                ),
                CustomerEntryUiState(
                    route = route,
                    isLoading = true,
                    name = "Kibera",
                    email = "customer@example.com",
                    phone = "+254712345678",
                ),
            )
        }
}

@XentlyPreview
@Composable
private fun CustomerEntryScreenPreview(
    @PreviewParameter(CustomerEntryUiStateParameterProvider::class)
    uiState: CustomerEntryUiState,
) {
    KwanzaTukuleTheme {
        CustomerEntryScreen(
            component = CustomerEntryComponent.Fake(uiState),
            modifier = Modifier.fillMaxSize(),
        )
    }
}