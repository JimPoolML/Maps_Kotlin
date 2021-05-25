package appjpm4everyone.googleapiclass

data class MyPlaces(
    val html_attributions: List<Any>,
    val next_page_token: String,
    val results: List<Result>,
    val status: String
)