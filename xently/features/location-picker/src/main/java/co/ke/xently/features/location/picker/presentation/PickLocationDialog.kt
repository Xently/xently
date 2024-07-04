package co.ke.xently.features.location.picker.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import co.ke.xently.features.location.picker.R
import co.ke.xently.libraries.location.tracker.domain.Location

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun PickLocationDialog(
    location: Location,
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit,
    onLocationChange: (Location) -> Unit,
) {
    var positionMarkerAtTheCentre by rememberSaveable { mutableStateOf(true) }
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            decorFitsSystemWindows = false,
            usePlatformDefaultWidth = false,
        ),
    ) {
        PickLocationScreen(
            modifier = modifier,
            location = location,
            positionMarkerAtTheCentre = positionMarkerAtTheCentre,
            onClickConfirmSelection = onDismissRequest,
            onLocationChange = onLocationChange,
        ) {
            TopAppBar(
                title = {
                    Text(text = stringResource(R.string.appbar_title_pick_store_location))
                },
                navigationIcon = {
                    IconButton(
                        onClick = onDismissRequest,
                        content = {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = stringResource(R.string.action_close),
                            )
                        },
                    )
                },
                actions = {
                    TextButton(
                        content = { Text(text = stringResource(R.string.action_center_marker)) },
                        onClick = { positionMarkerAtTheCentre = !positionMarkerAtTheCentre },
                    )
                },
            )
        }
    }
}