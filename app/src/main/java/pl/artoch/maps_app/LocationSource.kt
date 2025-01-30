package pl.artoch.maps_app

import android.location.Location
import com.google.android.gms.maps.LocationSource

object LocationSource : LocationSource {
    private var currentListener: LocationSource.OnLocationChangedListener? = null

    override fun activate(listener: LocationSource.OnLocationChangedListener) {
        currentListener = listener
    }

    override fun deactivate() {
        currentListener = null
    }

    fun onNewLocation(location: Location) {
        currentListener?.onLocationChanged(location)
    }
}