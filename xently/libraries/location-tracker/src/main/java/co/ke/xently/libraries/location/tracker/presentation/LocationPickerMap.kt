package co.ke.xently.libraries.location.tracker.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.ke.xently.libraries.location.tracker.domain.Location
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.google.maps.android.compose.widgets.ScaleBar

private fun LatLng.toXentlyLocation(): Location {
    return Location(latitude = latitude, longitude = longitude)
}

@Composable
fun LocationPickerMap(
    modifier: Modifier,
    location: Location?,
    positionMarkerAtTheCentre: Boolean,
    enableMyLocation: Boolean,
    contentDescription: String? = null,
    onPositionMarkerAtTheCentreChange: (Boolean) -> Unit,
    onMarkerPositionChange: (Location) -> Unit,
) {
    val markerState = rememberMarkerState()
    val cameraPositionState: CameraPositionState = rememberCameraPositionState()

    LaunchedEffect(location) {
        if (location != null) {
            LatLng(location.latitude, location.longitude).let {
                markerState.position = it
                cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 18f)
            }
        }
    }

    if (markerState.isDragging) {
        LaunchedEffect(markerState.position) {
            onMarkerPositionChange(markerState.position.toXentlyLocation())
            onPositionMarkerAtTheCentreChange(true)
        }
    }

    if (positionMarkerAtTheCentre) {
        LaunchedEffect(markerState.position) {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(markerState.position, 18f)
//            onPositionMarkerAtTheCentreChange(false)
        }
    }

    Box(modifier = modifier) {
        val properties = remember(enableMyLocation) {
            MapProperties(isMyLocationEnabled = enableMyLocation)
        }
        val uiSettings = remember(enableMyLocation) {
            MapUiSettings(myLocationButtonEnabled = enableMyLocation)
        }
        GoogleMap(
            properties = properties,
            uiSettings = uiSettings,
            contentDescription = contentDescription,
            cameraPositionState = cameraPositionState,
            modifier = Modifier.matchParentSize(),
            onMapClick = {
                onMarkerPositionChange(it.toXentlyLocation())
                onPositionMarkerAtTheCentreChange(true)
            },
            onPOIClick = {
                onMarkerPositionChange(it.latLng.toXentlyLocation())
                onPositionMarkerAtTheCentreChange(true)
            },
        ) {
            Marker(
                draggable = true,
                state = markerState,
                visible = location != null,
            )
        }

        ScaleBar(
            modifier = Modifier
                .padding(top = 5.dp, end = 15.dp)
                .align(Alignment.TopStart),
            cameraPositionState = cameraPositionState,
        )
    }
}