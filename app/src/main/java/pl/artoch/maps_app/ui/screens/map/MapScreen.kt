@file:OptIn(ExperimentalPermissionsApi::class)

package pl.artoch.maps_app.ui.screens.map

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.ComposeMapColorScheme
import com.google.maps.android.compose.DefaultMapProperties
import com.google.maps.android.compose.DefaultMapUiSettings
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import pl.artoch.maps_app.R
import pl.artoch.maps_app.permissions.Permission
import pl.artoch.maps_app.ui.ObserveEvents
import pl.artoch.maps_app.ui.components.Icon
import pl.artoch.maps_app.ui.screens.map.MapContract.Event.AskForMapPermissions
import pl.artoch.maps_app.ui.screens.map.MapContract.Event.ChangeCameraPosition
import pl.artoch.maps_app.ui.theme.MyApplicationTheme

@Composable
fun MapScreen(viewModel: MapContract.ViewModel, innerPadding: PaddingValues) {
    val viewState by viewModel.viewState.collectAsState()
    MapScreen(
        viewState = viewState,
        events = viewModel.events,
        innerPadding = innerPadding,
        onTargetButtonClick = viewModel::onTargetButtonClick,
        onPermissionStateChange = viewModel::onPermissionStateChange,
        onCameraPositionChange = viewModel::onCameraPositionChange,
        onDispose = viewModel::onDispose
    )
}

@Composable
private fun MapScreen(
    viewState: MapContract.ViewState,
    events: Flow<MapContract.Event>,
    innerPadding: PaddingValues,
    onTargetButtonClick: () -> Unit,
    onPermissionStateChange: (Boolean) -> Unit,
    onCameraPositionChange: (CameraPosition) -> Unit,
    onDispose: () -> Unit
) {
    val cameraPosition = rememberCameraPositionState()
    val permissionsState = rememberMultiplePermissionsState(Permission.mapPermissions())
    DisposableEffect(permissionsState.allPermissionsGranted) {
        onPermissionStateChange(permissionsState.allPermissionsGranted)
        onDispose(onDispose)
    }
    ObserveEvents(events) { event ->
        when (event) {
            is ChangeCameraPosition -> cameraPosition.animateTo(event.newPosition)
            is AskForMapPermissions -> permissionsState.launchMultiplePermissionRequest()
        }
    }
    LaunchedEffect(cameraPosition.isMoving) {
        onCameraPositionChange(cameraPosition.position)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .consumeWindowInsets(innerPadding)
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding,
            cameraPositionState = cameraPosition,
            properties = setupMapProperties(viewState.isMapPermissionGranted),
            uiSettings = setupMapUiSettings(),
            mapColorScheme = ComposeMapColorScheme.FOLLOW_SYSTEM
        )
        TargetButton(
            isPermissionsGranted = viewState.isMapPermissionGranted,
            modifier = Modifier
                .padding(end = 16.dp, bottom = innerPadding.calculateBottomPadding() + 16.dp)
                .align(Alignment.BottomEnd),
            onClick = onTargetButtonClick
        )
    }
}

@Composable
private fun TargetButton(isPermissionsGranted: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        shape = CircleShape,
        contentColor = Color.White,
        containerColor = contentColor(isPermissionsGranted)
    ) {
        Icon(iconRes = R.drawable.ic_target)
    }
}

private fun contentColor(isPermitted: Boolean) = if (isPermitted) Color(0xFF1C73E8) else Color.Gray

private fun setupMapProperties(isPermitted: Boolean) = DefaultMapProperties.copy(isMyLocationEnabled = isPermitted)

private fun setupMapUiSettings() = DefaultMapUiSettings.copy(
    indoorLevelPickerEnabled = false,
    myLocationButtonEnabled = false,
    zoomControlsEnabled = false
)

private suspend fun CameraPositionState.animateTo(newPosition: CameraPosition) = animate(
    CameraUpdateFactory.newCameraPosition(newPosition),
    ANIMATION_DURATION
)

private const val ANIMATION_DURATION = 1000

@Preview(showBackground = true)
@Composable
private fun MapScreenPreview() {
    MyApplicationTheme {
        MapScreen(
            viewState = MapContract.ViewState(isMapPermissionGranted = true),
            events = MutableSharedFlow(replay = 0),
            innerPadding = PaddingValues(),
            onTargetButtonClick = {},
            onCameraPositionChange = {},
            onPermissionStateChange = {},
            onDispose = {}
        )
    }
}
