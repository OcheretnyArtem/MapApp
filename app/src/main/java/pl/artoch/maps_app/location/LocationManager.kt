package pl.artoch.maps_app.location

import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Looper
import androidx.core.app.ActivityCompat.checkSelfPermission
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.qualifiers.ApplicationContext
import pl.artoch.maps_app.permissions.Permission
import javax.inject.Inject

interface LocationManager {

    fun requestLocationUpdates(onNewLocation: (LatLng) -> Unit)

    fun stopLocationUpdates()
}

class LocationManagerImpl @Inject constructor(@ApplicationContext private val context: Context) : LocationManager {

    private var onNewLocation: ((LatLng) -> Unit)? = null
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.locations.forEach { location ->
                onNewLocation?.invoke(LatLng(location.latitude, location.longitude))
            }
        }
    }

    override fun requestLocationUpdates(onNewLocation: (LatLng) -> Unit) {
        if (Permission.mapPermissions().all { checkSelfPermission(context, it) == PERMISSION_GRANTED }) {
            val locationRequest = LocationRequest.Builder(LOCATION_REQUEST_INTERVAL).build()
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
            this.onNewLocation = onNewLocation
        }
    }

    override fun stopLocationUpdates() {
        onNewLocation = null
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private companion object {
        private const val LOCATION_REQUEST_INTERVAL = 1000L
    }
}
