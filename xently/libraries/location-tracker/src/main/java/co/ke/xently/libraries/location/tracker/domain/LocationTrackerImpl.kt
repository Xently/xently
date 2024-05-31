package co.ke.xently.libraries.location.tracker.domain

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.CancellationSignal
import androidx.core.content.ContextCompat
import co.ke.xently.libraries.location.tracker.data.LocationSettingDelegate
import co.ke.xently.libraries.location.tracker.domain.error.Error
import co.ke.xently.libraries.location.tracker.domain.error.LocationRequestError
import co.ke.xently.libraries.location.tracker.domain.error.PermissionError
import co.ke.xently.libraries.location.tracker.domain.error.Result
import com.google.android.gms.location.FusedLocationProviderClient
import dagger.hilt.android.qualifiers.ApplicationContext
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

    private fun isGPSEnabled(): Boolean {
        return (context.getSystemService(Context.LOCATION_SERVICE) as LocationManager).run {
            isProviderEnabled(LocationManager.GPS_PROVIDER)
                    || isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }
    }

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

            client.lastLocation.addOnSuccessListener {
                val res: Result<Location, Error> = if (it == null) {
                    Result.Failure(LocationRequestError.NO_KNOWN_LOCATION)
                } else {
                    val location = Location(
                        latitude = it.latitude,
                        longitude = it.longitude,
                    )
                    currentLocation = location
                    Result.Success(location)
                }
                continuation.resume(res) {}
            }.addOnFailureListener {
                val error = if (it is SecurityException) {
                    Timber.e(it, "Security exception")
                    PermissionError.PERMISSION_DENIED
                } else {
                    Timber.e(it, "Unexpected error")
                    LocationRequestError.UNKNOWN
                }
                continuation.resume(Result.Failure(error)) {}
            }.addOnCanceledListener {
                continuation.cancel() // Cancel the coroutine
            }
        }
    }
}