package com.kwanzatukule.features.delivery.route.presentation.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.arkivanov.decompose.extensions.compose.subscribeAsState
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
import com.kwanzatukule.features.order.presentation.list.OrderListComponent
import com.kwanzatukule.libraries.location.tracker.presentation.ForegroundLocationTracker
import com.kwanzatukule.libraries.location.tracker.presentation.rememberLocationPermissionLauncher

@Composable
internal fun OrderMap(
    component: OrderListComponent,
    modifier: Modifier,
) {
    val state by component.uiState.subscribeAsState()
    val orders = component.orders.collectAsLazyPagingItems()

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

    if (!locationPermissionGranted) {
        LaunchedEffect(Unit) {
            locationPermissionLauncher.launch()
        }
    }

    val cameraPositionState: CameraPositionState = rememberCameraPositionState()

    LaunchedEffect(state.location) {
        state.location?.let {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(it.latitude, it.longitude),
                12f,
            )
        }
    }

    Box(modifier = modifier) {
        val properties = remember(locationPermissionGranted) {
            MapProperties(isMyLocationEnabled = locationPermissionGranted)
        }
        val uiSettings = remember(locationPermissionGranted) {
            MapUiSettings(myLocationButtonEnabled = locationPermissionGranted)
        }
        GoogleMap(
            properties = properties,
            uiSettings = uiSettings,
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
        ) {
            repeat(orders.itemCount) {
                orders[it]?.takeIf { order -> order.customer.location.isUsable() }?.let { order ->
                    val markerState = rememberMarkerState(
                        position = LatLng(
                            order.customer.location.latitude,
                            order.customer.location.longitude
                        ),
                    )
                    Marker(
                        state = markerState,
                        title = order.customer.name,
                        snippet = order.route.description,
                    )
                }
            }
        }

        ScaleBar(
            modifier = Modifier
                .padding(top = 5.dp, end = 15.dp)
                .align(Alignment.TopStart),
            cameraPositionState = cameraPositionState,
        )
    }
}