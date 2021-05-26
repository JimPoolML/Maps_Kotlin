package appjpm4everyone.geocodewaypoints

data class GeocodedWaypoints(
    val geocoded_waypoints: List<GeocodedWaypoint>,
    val routes: List<Route>,
    val status: String
)