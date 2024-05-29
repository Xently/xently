package com.kwanzatukule.libraries.location.tracker.presentation

import android.annotation.SuppressLint
import android.os.Looper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.kwanzatukule.libraries.location.tracker.data.LocationSettingDelegate
import com.kwanzatukule.libraries.location.tracker.domain.Location
import com.kwanzatukule.libraries.location.tracker.domain.Settings

@SuppressLint("MissingPermission")
@Composable
inline fun ForegroundLocationTracker(
    settings: Settings = remember { Settings() },
    crossinline onLocationUpdates: (Location) -> Unit,
) {
    val context = LocalContext.current
    val client = remember(context) {
        LocationServices.getFusedLocationProviderClient(context)
    }
    var currentLocation by LocationSettingDelegate(null)
    DisposableEffect(client) {
        val locationRequest = LocationRequest.Builder(
            settings.accuracy.priority,
            settings.refreshInterval.inWholeMilliseconds,
        ).build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                (p0.lastLocation?.run {
                    Location(
                        latitude = latitude,
                        longitude = longitude,
                    ).also { currentLocation = it }
                } ?: currentLocation)?.also(onLocationUpdates)
            }
        }

        client.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper(),
        )

        onDispose {
            client.removeLocationUpdates(locationCallback)
        }
    }
}