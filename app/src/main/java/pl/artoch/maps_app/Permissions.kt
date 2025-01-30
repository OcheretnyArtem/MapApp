package pl.artoch.maps_app

enum class Permissions(val id: String) {
    FINE_LOCATION(android.Manifest.permission.ACCESS_FINE_LOCATION),
    COARSE_LOCATION(android.Manifest.permission.ACCESS_COARSE_LOCATION);

    companion object {

        fun mapPermissions() = listOf(FINE_LOCATION, COARSE_LOCATION).map(Permissions::id)
    }
}
