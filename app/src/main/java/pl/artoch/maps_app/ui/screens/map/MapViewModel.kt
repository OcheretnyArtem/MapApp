package pl.artoch.maps_app.ui.screens.map

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import pl.artoch.maps_app.location.LocationManager
import pl.artoch.maps_app.ui.screens.map.MapContract.Event.ChangeCameraPosition
import javax.inject.Inject

interface MapContract {
    interface ViewModel {
        val viewState: StateFlow<ViewState>
        val events: Flow<Event>

        fun onTargetButtonClick()

        fun onPermissionStateChange(isGranted: Boolean)

        fun onCameraPositionChange(position: CameraPosition)

        fun onDispose()
    }

    data class ViewState(val isMapPermissionGranted: Boolean)

    sealed interface Event {
        data class ChangeCameraPosition(val newPosition: CameraPosition) : Event
        data object AskForMapPermissions : Event
    }
}

@HiltViewModel
class MapViewModel @Inject constructor(
    private val locationManager: LocationManager
) : ViewModel(), MapContract.ViewModel {
    private val eventsChanel = Channel<MapContract.Event>()
    private val currentLocation = MutableStateFlow(locationWarsaw)
    private val currentCameraPosition = MutableStateFlow(initialCameraPosition())
    override val viewState = MutableStateFlow(MapContract.ViewState(false))
    override val events = eventsChanel.receiveAsFlow()

    override fun onTargetButtonClick() {
        if (viewState.value.isMapPermissionGranted) {
            eventsChanel.trySend(ChangeCameraPosition(currentCameraPosition.value.updateCoordinates(currentLocation.value)))
        } else {
            eventsChanel.trySend(MapContract.Event.AskForMapPermissions)
        }
    }

    override fun onPermissionStateChange(isGranted: Boolean) {
        viewState.update { it.copy(isMapPermissionGranted = isGranted) }
        if (isGranted) {
            startObservingLocation()
        }
    }

    override fun onCameraPositionChange(position: CameraPosition) = currentCameraPosition.update { position }

    override fun onDispose() = locationManager.stopLocationUpdates()

    private fun startObservingLocation() = locationManager.requestLocationUpdates { location ->
        currentLocation.value = location
    }

    private fun initialCameraPosition() = CameraPosition(currentLocation.value, DEFAULT_ZOOM, 0f, 0f)

    private fun CameraPosition.updateCoordinates(coordinates: LatLng): CameraPosition {
        val zoom = zoom.takeIf { it > DEFAULT_ZOOM } ?: DEFAULT_ZOOM
        return CameraPosition.fromLatLngZoom(coordinates, zoom)
    }

    private companion object {
        private val locationWarsaw = LatLng(52.2297, 21.0122)
        private const val DEFAULT_ZOOM = 12f
    }
}
