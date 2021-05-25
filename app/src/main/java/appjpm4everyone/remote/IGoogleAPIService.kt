package appjpm4everyone.remote

import appjpm4everyone.googleapiclass.MyPlaces
import appjpm4everyone.googleapiclass.PlaceDetail
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface IGoogleAPIService {
    @GET
    fun getNearbyPlaces(@Url url: String): Call<MyPlaces>

    @GET
    fun getPlaceDetail(@Url url: String): Call<PlaceDetail>
}