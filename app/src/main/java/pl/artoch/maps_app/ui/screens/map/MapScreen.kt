@file:OptIn(ExperimentalPermissionsApi::class)

package pl.artoch.maps_app.ui.screens.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.ComposeMapColorScheme
import com.google.maps.android.compose.DefaultMapProperties
import com.google.maps.android.compose.DefaultMapUiSettings
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import pl.artoch.maps_app.R
import pl.artoch.maps_app.permissions.Permission
import pl.artoch.maps_app.ui.screens.map.MapContract.Event.ChangeCameraPosition
import pl.artoch.maps_app.ui.theme.MyApplicationTheme

@Composable
fun MapScreen(viewModel: MapContract.ViewModel = hiltViewModel<MapViewModel>()) {
    val viewState by viewModel.viewState.collectAsState()
    MapScreen(
        viewState = viewState,
        events = viewModel.events,
        onButtonClick = viewModel::onTargetButtonClick,
        onPermissionStateChange = viewModel::onPermissionStateChange,
        onDispose = viewModel::onDispose,
        onCameraPositionChange = viewModel::onCameraPositionChange
    )
}

@Composable
fun MapScreen(
    viewState: MapContract.ViewState,
    events: Flow<MapContract.Event>,
    onButtonClick: () -> Unit,
    onPermissionStateChange: (Boolean) -> Unit,
    onDispose: () -> Unit,
    onCameraPositionChange: (CameraPosition) -> Unit,
) {
    val cameraPosition = rememberCameraPositionState()
    val permissionsState = rememberMultiplePermissionsState(Permission.mapPermissions())
    ObserveEvents(events) { event ->
        when (event) {
            is ChangeCameraPosition -> cameraPosition.animate(
                CameraUpdateFactory.newCameraPosition(event.newPosition),
                1000
            )
        }
    }
    LaunchedEffect(cameraPosition.isMoving) {
        onCameraPositionChange(cameraPosition.position)
    }
    DisposableEffect(permissionsState.allPermissionsGranted) {
        onPermissionStateChange(permissionsState.allPermissionsGranted)
        onDispose(onDispose)
    }
    val onTargetButtonClick = {
        if (viewState.isMapPermissionGranted) onButtonClick() else permissionsState.launchMultiplePermissionRequest()
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = { TargetButton(viewState.isMapPermissionGranted, onTargetButtonClick) }
    ) { innerPadding ->
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding,
            cameraPositionState = cameraPosition,
            properties = setupMapProperties(viewState.isMapPermissionGranted),
            uiSettings = setupMapUiSettings(),
            mapColorScheme = ComposeMapColorScheme.FOLLOW_SYSTEM
        )
    }
}

@Composable
fun <T> ObserveEvents(flow: Flow<T>, onEvent: suspend (T) -> Unit) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect(onEvent)
        }
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
        MapScreen(
            viewState = MapContract.ViewState(isMapPermissionGranted = true),
            events = MutableSharedFlow(replay = 0),
            onButtonClick = {},
            onCameraPositionChange = {},
            onPermissionStateChange = {},
            onDispose = {}
        )
    }
}
