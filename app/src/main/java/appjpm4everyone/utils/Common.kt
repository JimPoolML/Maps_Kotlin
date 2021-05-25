package appjpm4everyone.utils

import appjpm4everyone.googleapiclass.Result
import appjpm4everyone.remote.IGoogleAPIService
import appjpm4everyone.remote.RetrofitClient

object Common {
    //Base URL
    private val GOOGLE_API_URL = "https://maps.googleapis.com/"

    //
    var currentResult: Result? = null

    //Now I can use this service to fetch API
    val googleAPIService: IGoogleAPIService
        get() = RetrofitClient.getClient(GOOGLE_API_URL).create(IGoogleAPIService::class.java)
}