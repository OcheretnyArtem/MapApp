package pl.artoch.maps_app.domain

data class Story(
    val url: String,
    val type: Type
) {

    enum class Type {
        VIDEO, IMAGE
    }
}
