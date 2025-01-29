@file:OptIn(ExperimentalPermissionsApi::class)

package pl.artoch.maps_app

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.os.Bundle
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.checkSelfPermission
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.LocationSource
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapColorScheme.FOLLOW_SYSTEM
import com.google.maps.android.compose.DefaultMapProperties
import com.google.maps.android.compose.DefaultMapUiSettings
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import pl.artoch.maps_app.ui.theme.MyApplicationTheme

class MapsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MapScreen()
            }
        }
    }
}

@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    initialCoordinates: LatLng = LatLng(52.237, 21.017)
) {
    val context = LocalContext.current
    val currentCoordinates = remember { mutableStateOf(initialCoordinates) }
    val cameraPosition = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentCoordinates.value, 15f)
    }
    val mapPermissions = listOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
    val locationPermissions = rememberMultiplePermissionsState(mapPermissions)
    val focusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    val locationSource = object : LocationSource {
        private var listener: LocationSource.OnLocationChangedListener? = null

        override fun activate(listener: LocationSource.OnLocationChangedListener) {
            this.listener = listener
        }

        override fun deactivate() {
            this.listener = null
        }

        fun onNewLocation(location: Location) {
            listener?.onLocationChanged(location)
        }
    }

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.locations.forEach(locationSource::onNewLocation)
        }
    }

    DisposableEffect(locationPermissions) {
        println("DisposableEffect START")
        if (mapPermissions.all { checkSelfPermission(context, it) == PERMISSION_GRANTED }) {
            focusedLocationClient.requestLocationUpdates(
                LocationRequest.Builder(1000L).build(),
                locationCallback,
                Looper.getMainLooper()
            )
            println("DisposableEffect PERMISSIONS GRANDER")
        }
        onDispose {
            println("DisposableEffect STOP")
            locationSource.deactivate()
            focusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (locationPermissions.allPermissionsGranted) {
                        cameraPosition.position = CameraPosition.fromLatLngZoom(currentCoordinates.value, cameraPosition.position.zoom)
                    } else {
                        locationPermissions.launchMultiplePermissionRequest()
                    }
                },
                shape = CircleShape,
                contentColor = Color.White,
                containerColor = contentColor(locationPermissions.allPermissionsGranted)
            ) {
                Icon(
                    modifier = Modifier.sizeIn(24.dp),
                    painter = painterResource(id = R.drawable.ic_target),
                    contentDescription = ""
                )
            }
        }
    ) { innerPadding ->
        GoogleMap(
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding,
            cameraPositionState = cameraPosition,
            googleMapOptionsFactory = ::setupMapColorScheme,
            properties = setupMapProperties(locationPermissions.allPermissionsGranted),
            uiSettings = setupMapUiSettings(),
            locationSource = locationSource
        )
    }
}

private fun contentColor(
    isLocationPermissionsGranted: Boolean
) = if (isLocationPermissionsGranted) Color(0xFF1C73E8) else Color.Gray

private fun setupMapColorScheme() = GoogleMapOptions().apply { mapColorScheme(FOLLOW_SYSTEM) }

private fun setupMapProperties(isPermissionGranted: Boolean) =
    DefaultMapProperties.copy(isMyLocationEnabled = isPermissionGranted)

private fun setupMapUiSettings() = DefaultMapUiSettings.copy(
    indoorLevelPickerEnabled = false,
    myLocationButtonEnabled = false,
    zoomControlsEnabled = false
)

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme {
        MapScreen()
    }
}
