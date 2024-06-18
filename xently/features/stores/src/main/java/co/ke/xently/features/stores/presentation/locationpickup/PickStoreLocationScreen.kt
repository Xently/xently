package co.ke.xently.features.stores.presentation.locationpickup

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import co.ke.xently.features.stores.R
import co.ke.xently.features.stores.presentation.locationpickup.components.PickLocationBottomBarCard
import co.ke.xently.features.ui.core.presentation.theme.XentlyTheme
import co.ke.xently.libraries.location.tracker.domain.Location
import co.ke.xently.libraries.location.tracker.presentation.ForegroundLocationTracker
import co.ke.xently.libraries.location.tracker.presentation.LocationPickerMap
import co.ke.xently.libraries.location.tracker.presentation.rememberLocationPermissionLauncher
import co.ke.xently.libraries.ui.core.XentlyPreview
import co.ke.xently.libraries.ui.core.components.NavigateBackIconButton
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickStoreLocationScreen(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit,
) {
    val viewModel = hiltViewModel<PickStoreLocationViewModel>()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val event by viewModel.event.collectAsStateWithLifecycle(null)
    var positionMarkerAtTheCentre by rememberSaveable { mutableStateOf(true) }

    LaunchedEffect(event) {
        when (event) {
            null -> Unit
            PickStoreLocationEvent.SelectionConfirmed -> onClickBack()
        }
    }

    PickStoreLocationScreen(
        modifier = modifier,
        location = state.location,
        positionMarkerAtTheCentre = positionMarkerAtTheCentre,
        onLocationChange = { viewModel.onAction(PickStoreLocationAction.UpdateLocation(it)) },
        onClickConfirmSelection = { viewModel.onAction(PickStoreLocationAction.ConfirmSelection) },
    ) {
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
    }
}

@Composable
internal fun PickStoreLocationScreen(
    location: Location?,
    modifier: Modifier = Modifier,
    positionMarkerAtTheCentre: Boolean = true,
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    onClickConfirmSelection: () -> Unit,
    onLocationChange: (Location) -> Unit,
    topBar: @Composable () -> Unit = {},
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val scope = rememberCoroutineScope()

    var locationPermissionGranted by remember {
        mutableStateOf(false)
    }

    var shouldTrackLocation by remember {
        mutableStateOf(false)
    }
    val locationPermissionLauncher = rememberLocationPermissionLauncher {
        locationPermissionGranted = it
    }

    if (shouldTrackLocation && locationPermissionGranted) {
        ForegroundLocationTracker(onLocationUpdates = onLocationChange)
    }

    Scaffold(
        modifier = modifier,
        topBar = topBar,
        contentWindowInsets = contentWindowInsets,
        bottomBar = {
            PickLocationBottomBarCard(
                snackbarHostState = snackbarHostState,
                enableConfirmSelection = location != null,
                onClickConfirmSelection = onClickConfirmSelection,
                onClickUseMyLocation = {
                    if (!locationPermissionGranted) {
                        scope.launch {
                            locationPermissionLauncher.launch()
                        }
                    }
                    shouldTrackLocation = true
                },
            )
        },
    ) { paddingValues ->
        LocationPickerMap(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            location = location,
            positionMarkerAtTheCentre = positionMarkerAtTheCentre,
            enableMyLocation = locationPermissionGranted,
            onMarkerPositionChange = onLocationChange,
        )
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
            location = state.location,
            modifier = Modifier.fillMaxSize(),
            onClickConfirmSelection = {},
            onLocationChange = {},
        )
    }
}