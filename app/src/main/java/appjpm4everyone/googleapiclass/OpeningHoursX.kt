package appjpm4everyone.googleapiclass

data class OpeningHoursX(
    val open_now: Boolean,
    val periods: List<Period>,
    val weekday_text: List<String>
)