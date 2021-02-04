package com.bladerco.jmappy.model.data.places

data class PlacesResponse(
    val html_attributions: List<Any>,
    val next_page_token: String,
    val results: List<PlacesResult>,
    val status: String
)


data class Southwest(
    val lat: Double,
    val lng: Double
)

data class Viewport(
    val northeast: Northeast,
    val southwest: Southwest
)

data class Geometry(
    val location: Location,
    val viewport: Viewport
)

data class Location(
    val lat: Double,
    val lng: Double
)
data class Northeast(
    val lat: Double,
    val lng: Double
)
data class OpeningHours(
    val open_now: Boolean
)
data class Photo(
    val height: Int,
    val html_attributions: List<String>,
    val photo_reference: String,
    val width: Int
)

data class PlusCode(
    val compound_code: String,
    val global_code: String
)