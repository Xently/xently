package com.kwanzatukule.features.customer.presentation.entry.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.DragState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.kwanzatukule.features.customer.R
import com.kwanzatukule.libraries.location.tracker.domain.Location

@Composable
internal inline fun CustomerLocationPickerMap(
    modifier: Modifier,
    location: Location?,
    positionMarkerAtTheCentre: Boolean,
    enableMyLocation: Boolean,
    crossinline onMarkerPositionChange: (Location) -> Unit,
) {
    val markerState = rememberMarkerState()
    val cameraPositionState: CameraPositionState = rememberCameraPositionState()

    LaunchedEffect(location, positionMarkerAtTheCentre) {
        if (location != null) {
            LatLng(location.latitude, location.longitude).let {
                markerState.position = it
                cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 18f)
            }
        }
    }

    var wasMarkerDragStarted by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(markerState.dragState) {
        when (markerState.dragState) {
            DragState.END -> {
                if (wasMarkerDragStarted) {
                    // DragState.END is called by default, but we only want to
                    // update the location state if the drag was actually
                    // triggered. A triggered drag must first have the state
                    // as START.
                    Location(
                        latitude = markerState.position.latitude,
                        longitude = markerState.position.longitude,
                    ).let(onMarkerPositionChange)

                    wasMarkerDragStarted = false
                }
            }

            DragState.START -> {
                wasMarkerDragStarted = true
            }

            DragState.DRAG -> {
            }
        }
    }

    GoogleMap(
        modifier = modifier,
        properties = remember { MapProperties(isMyLocationEnabled = enableMyLocation) },
        uiSettings = remember { MapUiSettings(myLocationButtonEnabled = enableMyLocation) },
        cameraPositionState = cameraPositionState,
        contentDescription = stringResource(R.string.content_desc_customer_entry_map),
        onMapClick = {
            Location(
                latitude = it.latitude,
                longitude = it.longitude,
            ).let(onMarkerPositionChange)
        },
        onPOIClick = { poi: PointOfInterest ->
            // TODO: Consider if store name should be overridden if already provided
            //  like we currently do
            val name = poi.name.split("\n").joinToString {
                it.trim()
            }

            Location(
                name = name,
                latitude = poi.latLng.latitude,
                longitude = poi.latLng.longitude,
            ).let(onMarkerPositionChange)
        },
    ) {
        Marker(
            state = markerState,
            draggable = true,
            visible = location != null,
            onClick = {
                Location(
                    latitude = it.position.latitude,
                    longitude = it.position.longitude,
                ).let(onMarkerPositionChange)
                true
            },
        )
    }
}