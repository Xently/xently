package co.ke.xently.libraries.location.tracker.domain

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.CancellationSignal
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import co.ke.xently.libraries.location.tracker.data.LocationSettingDelegate
import co.ke.xently.libraries.location.tracker.domain.error.Error
import co.ke.xently.libraries.location.tracker.domain.error.LocationRequestError
import co.ke.xently.libraries.location.tracker.domain.error.PermissionError
import co.ke.xently.libraries.location.tracker.domain.error.Result
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationTrackerImpl @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val client: FusedLocationProviderClient,
) : LocationTracker {
    private var currentLocation by LocationSettingDelegate(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getCurrentLocation(): Result<Location, Error> {
        if (!isGPSEnabled()) return Result.Failure(PermissionError.GPS_DISABLED)

        val hasAccessFineLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasAccessCoarseLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!(hasAccessCoarseLocationPermission || hasAccessFineLocationPermission)) {
            return Result.Failure(PermissionError.PERMISSION_DENIED)
        }

        Timber.i("Getting current device location...")
        return suspendCancellableCoroutine { continuation ->
            val cancellationSignal = CancellationSignal()

            continuation.invokeOnCancellation { cancellationSignal.cancel() }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                continuation.getBelowRLocation()
            } else {
                val locationManager = context.getSystemService<LocationManager>()!!

                locationManager.getCurrentLocation(
                    LocationManager.NETWORK_PROVIDER,
                    cancellationSignal,
                    context.mainExecutor,
                ) {
                    continuation.resume(it.toLocationResult()) {}
                }
            }
        }
    }

    private fun isGPSEnabled(): Boolean {
        return (context.getSystemService(Context.LOCATION_SERVICE) as LocationManager).run {
            isProviderEnabled(LocationManager.GPS_PROVIDER)
                    || isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
    private fun CancellableContinuation<Result<Location, Error>>.getBelowRLocation() {
        client.lastLocation.addOnSuccessListener {
            resume(it.toLocationResult()) {}
        }.addOnFailureListener {
            val error = if (it is SecurityException) {
                Timber.e(it, "Security exception")
                PermissionError.PERMISSION_DENIED
            } else {
                Timber.e(it, "Unexpected error")
                LocationRequestError.UNKNOWN
            }
            resume(Result.Failure(error)) {}
        }.addOnCanceledListener {
            cancel() // Cancel the coroutine
        }
    }

    private fun android.location.Location.toLocationResult(): Result<Location, Error> {
        val location = Location(
            latitude = latitude,
            longitude = longitude,
        )
        currentLocation = location
        return Result.Success(location)
    }
}