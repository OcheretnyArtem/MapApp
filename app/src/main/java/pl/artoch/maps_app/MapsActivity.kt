@file:OptIn(ExperimentalPermissionsApi::class)

package pl.artoch.maps_app

import android.os.Bundle
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
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.ComposeMapColorScheme
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
    initialCoordinates: LatLng = LatLng(52.237, 21.017),
    initialCameraPosition: CameraPosition = CameraPosition.fromLatLngZoom(initialCoordinates, 15f)
) {
    val context = LocalContext.current
    val locationManager = remember { LocationManagerImpl(context = context) }
    val currentCoordinates = remember { mutableStateOf(initialCoordinates) }
    val cameraPosition = rememberCameraPositionState { position = initialCameraPosition }
    val locationPermissions = rememberMultiplePermissionsState(Permissions.mapPermissions())

    DisposableEffect(locationPermissions.allPermissionsGranted) {
        locationManager.requestLocationUpdates { location -> currentCoordinates.value = location }
        onDispose { locationManager.stopLocationUpdates() }
    }

    val onTargetButtonClick = {
        if (locationPermissions.allPermissionsGranted) {
            cameraPosition.position = cameraPosition.position.updateCoordinates(currentCoordinates.value)
        } else {
            locationPermissions.launchMultiplePermissionRequest()
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            TargetButton(
                isPermissionsGranted = locationPermissions.allPermissionsGranted,
                onClick = onTargetButtonClick
            )
        }
    ) { innerPadding ->
        GoogleMap(
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding,
            cameraPositionState = cameraPosition,
            properties = setupMapProperties(locationPermissions.allPermissionsGranted),
            uiSettings = setupMapUiSettings(),
            locationSource = LocationSource,
            mapColorScheme = ComposeMapColorScheme.FOLLOW_SYSTEM
        )
    }
}

@Composable
private fun TargetButton(isPermissionsGranted: Boolean, onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        shape = CircleShape,
        contentColor = Color.White,
        containerColor = contentColor(isPermissionsGranted)
    ) {
        Icon(
            modifier = Modifier.sizeIn(24.dp),
            painter = painterResource(id = R.drawable.ic_target),
            contentDescription = ""
        )
    }
}

private fun CameraPosition.updateCoordinates(coordinates: LatLng) = CameraPosition.fromLatLngZoom(coordinates, zoom)

private fun contentColor(isPermitted: Boolean) = if (isPermitted) Color(0xFF1C73E8) else Color.Gray

private fun setupMapProperties(isPermitted: Boolean) = DefaultMapProperties.copy(isMyLocationEnabled = isPermitted)

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
