package co.ke.xently.libraries.location.tracker.domain

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.CancellationSignal
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import co.ke.xently.libraries.data.core.domain.DispatchersProvider
import co.ke.xently.libraries.location.tracker.data.LocationSettingDelegate
import co.ke.xently.libraries.location.tracker.domain.error.Error
import co.ke.xently.libraries.location.tracker.domain.error.LocationRequestError
import co.ke.xently.libraries.location.tracker.domain.error.PermissionError
import co.ke.xently.libraries.location.tracker.domain.error.Result
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Singleton
class LocationTrackerImpl @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val dispatchersProvider: DispatchersProvider,
    private val client: FusedLocationProviderClient,
) : LocationTracker {
    private val locationManager by lazy { context.getSystemService<LocationManager>()!! }
    private var currentLocation by LocationSettingDelegate(null)

    companion object {
        private val TAG = LocationTracker::class.java.simpleName
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

        Timber.tag(TAG).i("Getting current device location...")
        return suspendCancellableCoroutine { continuation ->
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                continuation.getBelowRLocation()
            } else {
                val cancellationSignal = CancellationSignal()

                continuation.invokeOnCancellation { cancellationSignal.cancel() }

                locationManager.getCurrentLocation(
                    LocationManager.NETWORK_PROVIDER,
                    cancellationSignal,
                    context.mainExecutor,
                ) {
                    continuation.resume(it.toLocationResult()) { cause, _, _ -> }
                }
            }
        }
    }

    override fun observeLocation(
        interval: Duration?,
        minimumEmissionDistanceMeters: Int?,
        priority: LocationPriority,
        permissionBehaviour: MissingPermissionBehaviour,
    ) = callbackFlow {
        currentLocation?.let {
            send(it)
        }

        while (!isGPSEnabled()) {
            val duration = 3.seconds
            Timber.tag(TAG).d("GPS not enabled, waiting %s before rechecking...", duration)
            delay(duration)
        }

        var hasFineLocationPermission = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        var hasCoarseLocationPermission = ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        while (!(hasFineLocationPermission || hasCoarseLocationPermission) && permissionBehaviour == MissingPermissionBehaviour.REPEAT_CHECK) {
            hasFineLocationPermission = ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            hasCoarseLocationPermission = ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            val duration = 3.seconds
            Timber.tag(TAG)
                .d("Permissions not granted, waiting %s before rechecking...", duration)
            delay(duration)
        }

        if (!hasFineLocationPermission || !hasCoarseLocationPermission) {
            Timber.tag(TAG).d("Closing location tracking. Missing required permissions.")
            close()
        } else {
            Timber.tag(TAG).d("All set for location tracking! Adding location observers...")

            val request = LocationRequest.Builder(
                priority.value,
                (interval ?: priority.interval).inWholeMilliseconds,
            ).build()

            val callback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    super.onLocationResult(result)
                    result.locations.lastOrNull()?.let {
                        val distanceFromPreviousLocation =
                            currentLocation?.toAndroidLocation()?.distanceTo(it)?.toInt()
                        if (minimumEmissionDistanceMeters != null && distanceFromPreviousLocation != null && distanceFromPreviousLocation < minimumEmissionDistanceMeters) {
                            Timber.tag(TAG).d(
                                "Skipping location update. Distance too small - %sm < %sm.",
                                distanceFromPreviousLocation,
                                minimumEmissionDistanceMeters,
                            )

                            return@let
                        }
                        val location = it.toXentlyLocation()

                        Timber.tag(TAG).d("Current location: %s", location)
                        CoroutineScope(coroutineContext + dispatchersProvider.io).launch {
                            currentLocation = location
                        }
                        trySend(location)
                    }
                }
            }

            client.requestLocationUpdates(request, callback, context.mainLooper)

            awaitClose {
                Timber.tag(TAG).d("Removing location observers...")
                client.removeLocationUpdates(callback)
            }
        }
    }.distinctUntilChanged()

    private fun isGPSEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @RequiresPermission(anyOf = ["android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"])
    private fun CancellableContinuation<Result<Location, Error>>.getBelowRLocation() {
        client.lastLocation.addOnSuccessListener {
            resume(it.toLocationResult()) { cause, _, _ -> }
        }.addOnFailureListener {
            val error = if (it is SecurityException) {
                Timber.tag(TAG).e(it, "Security exception")
                PermissionError.PERMISSION_DENIED
            } else {
                Timber.tag(TAG).e(it, "Unexpected error")
                LocationRequestError.UNKNOWN
            }
            resume(Result.Failure(error)) { cause, _, _ -> }
        }.addOnCanceledListener {
            cancel() // Cancel the coroutine
        }
    }

    private fun android.location.Location.toLocationResult(): Result<Location, Error> {
        val location = toXentlyLocation()
        CoroutineScope(dispatchersProvider.io).launch {
            currentLocation = location
        }
        return Result.Success(location)
    }
}