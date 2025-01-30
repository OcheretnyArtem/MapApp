package pl.artoch.maps_app.permissions

enum class Permission(val id: String) {
    FINE_LOCATION(android.Manifest.permission.ACCESS_FINE_LOCATION),
    COARSE_LOCATION(android.Manifest.permission.ACCESS_COARSE_LOCATION);

    companion object {

        fun mapPermissions() = listOf(FINE_LOCATION, COARSE_LOCATION).map(Permission::id)
    }
}
