package appjpm4everyone.ui.viewplace

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import appjpm4everyone.base.BaseActivity
import appjpm4everyone.googleapiclass.PlaceDetail
import appjpm4everyone.remote.IGoogleAPIService
import appjpm4everyone.ui.mapskotlin.R
import appjpm4everyone.ui.mapskotlin.databinding.ActivityViewPlaceBinding
import appjpm4everyone.utils.Common
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.StringBuilder

class ViewPlaceActivity : BaseActivity() {

    //Implements DataBinding
    private lateinit var binding: ActivityViewPlaceBinding

    //Retrofit service
    internal lateinit var mService: IGoogleAPIService
    var mPlace: PlaceDetail? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //To use DataBinding
        binding = ActivityViewPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Init Service
        mService = Common.googleAPIService
        initUI()
    }

    @SuppressLint("SetTextI18n")
    private fun initUI() {
        binding.tvPlaceName.text = ""
        binding.tvPlaceAddress.text = ""
        binding.tvPlaceOpenHour.text = ""

        binding.btnShowMap.setOnClickListener {
            //Open Route Map Intent to MainActivity
            val intent = Intent()
            setResult(Activity.RESULT_OK, intent)
            finish()
            /*val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse(mPlace!!.result.url))
            startActivity(mapIntent)*/
        }

        //Load photo to place
        if (!Common.currentResult!!.photos.isNullOrEmpty() && Common.currentResult!!.photos.isNotEmpty()) {
            Picasso.with(this)
                .load(getPhotoPlace(Common.currentResult!!.photos!![0].photo_reference!!, 400))
                .into(binding.imgPlace)
        }

        //Load rating
        if (Common.currentResult!!.rating != null) {
            binding.ratingBar.rating = Common.currentResult!!.rating!!.toFloat()
        } else {
            binding.ratingBar.visibility = View.GONE
        }

        //Load open hours
        if (Common.currentResult!!.opening_hours != null) {
            binding.tvPlaceOpenHour.text =
                getString(R.string.place_hour) + Common.currentResult!!.opening_hours.toString()
        } else {
            binding.tvPlaceOpenHour.visibility = View.GONE
        }

        //Load open hours
        if (Common.currentResult!!.opening_hours != null) {
                if(Common.currentResult!!.opening_hours!!.open_now){
                    binding.tvPlaceOpenHour.text = getString(R.string.place_hour) + "SI"
                }else{
                    binding.tvPlaceOpenHour.text = getString(R.string.place_hour) + "NO"
                }
        } else {
            binding.tvPlaceOpenHour.visibility = View.GONE
        }

        getDetailPlace()
    }

    private fun getDetailPlace() {
        //Use service to fetch address and Name
        progressBar.show(this)
        mService.getPlaceDetail(getPlaceDetailUrl(Common.currentResult!!.place_id))
            .enqueue(object : Callback<PlaceDetail> {
                override fun onResponse(call: Call<PlaceDetail>, response: Response<PlaceDetail>) {
                    mPlace = response.body()
                    progressBar.hideProgress()
                    binding.tvPlaceAddress.text = mPlace!!.result.formatted_address
                    binding.tvPlaceName.text = mPlace!!.result.name
                }

                override fun onFailure(call: Call<PlaceDetail>, t: Throwable) {
                    showSnakyBar(t.message!!)
                    progressBar.hideProgress()
                }
            })
    }

    private fun showSnakyBar(message: String) {
        showLongSnackError(this, message)
    }

    private fun getPlaceDetailUrl(placeId: String): String {
        val url = StringBuilder("https://maps.googleapis.com/maps/api/place/details/json")
        url.append("?place_id=$placeId")
        url.append("&key=AIzaSyDJwSEcfq3ijM50mbd1axRb4uBJWR6vZAo")
        Log.d("Url Detail", url.toString())
        return url.toString()
    }

    private fun getPhotoPlace(photoReference: String, maxWidth: Int): String {
        val url = StringBuilder("https://maps.googleapis.com/maps/api/place/photo")
        url.append("?maxwidth=$maxWidth")
        url.append("&photoreference=$photoReference")
        url.append("&key=AIzaSyDJwSEcfq3ijM50mbd1axRb4uBJWR6vZAo")
        Log.d("Url Photo", url.toString())
        return url.toString()

        }


}