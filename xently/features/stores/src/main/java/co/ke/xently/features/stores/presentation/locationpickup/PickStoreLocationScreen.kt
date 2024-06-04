package co.ke.xently.features.stores.presentation.locationpickup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.ke.xently.features.stores.R
import co.ke.xently.features.ui.core.presentation.components.PrimaryButton
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.location.tracker.domain.Location
import co.ke.xently.libraries.location.tracker.presentation.ForegroundLocationTracker
import co.ke.xently.libraries.location.tracker.presentation.LocationPickerMap
import co.ke.xently.libraries.location.tracker.presentation.rememberLocationPermissionLauncher
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.core.components.NavigateBackIconButton
import kotlinx.coroutines.launch

@Composable
internal fun PickStoreLocationScreen(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
) {
    val viewModel = hiltViewModel<PickStoreLocationViewModel>()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    PickStoreLocationScreen(
        state = state,
        modifier = modifier,
        onAction = viewModel::onAction,
        onClickBack = onClickBack,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun PickStoreLocationScreen(
    state: PickStoreLocationUiState,
    modifier: Modifier = Modifier,
    onAction: (PickStoreLocationAction) -> Unit,
    onClickBack: () -> Unit,
) {
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val scope = rememberCoroutineScope()

    var positionMarkerAtTheCentre by remember {
        mutableStateOf(false)
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        fontWeight = FontWeight.Bold,
                        text = stringResource(R.string.appbar_title_pick_store_location),
                    )
                },
                navigationIcon = { NavigateBackIconButton(onClick = onClickBack) },
                actions = {
                    IconButton(
                        onClick = {
                            positionMarkerAtTheCentre = !positionMarkerAtTheCentre
                        },
                        content = {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                            )
                        },
                    )
                },
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
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
                ForegroundLocationTracker {
                    onAction(PickStoreLocationAction.UpdateLocation(it))
                }
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
            ) { Text(text = stringResource(R.string.action_use_my_current_location)) }

            LocationPickerMap(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                location = state.location,
                positionMarkerAtTheCentre = true,
                enableMyLocation = locationPermissionGranted,
                onMarkerPositionChange = { onAction(PickStoreLocationAction.UpdateLocation(it)) },
            )

            SnackbarHost(hostState = snackbarHostState)

            PrimaryButton(
                modifier = Modifier.fillMaxWidth(),
                shape = RectangleShape,
                enabled = state.location != null,
                label = stringResource(R.string.action_confirm_selection),
                onClick = { onAction(PickStoreLocationAction.ConfirmSelection) },
            )
        }
    }
}

private class PickStoreLocationUiStateParameterProvider :
    PreviewParameterProvider<PickStoreLocationUiState> {
    override val values: Sequence<PickStoreLocationUiState>
        get() = sequenceOf(
            PickStoreLocationUiState(),
            PickStoreLocationUiState(location = Location()),
            PickStoreLocationUiState(location = Location(latitude = 1.0, longitude = 2.0)),
        )
}

@XentlyPreview
@Composable
private fun PickStoreLocationScreenPreview(
    @PreviewParameter(PickStoreLocationUiStateParameterProvider::class)
    state: PickStoreLocationUiState,
) {
    XentlyTheme {
        PickStoreLocationScreen(
            state = state,
            modifier = Modifier.fillMaxSize(),
            onClickBack = {},
            onAction = {},
        )
    }
}