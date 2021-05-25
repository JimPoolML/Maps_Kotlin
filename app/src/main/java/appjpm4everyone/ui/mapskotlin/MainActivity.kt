package appjpm4everyone.ui.mapskotlin

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import appjpm4everyone.base.BaseActivity
import appjpm4everyone.googleapiclass.MyPlaces
import appjpm4everyone.remote.IGoogleAPIService
import appjpm4everyone.ui.mapskotlin.databinding.ActivityMainBinding
import appjpm4everyone.ui.viewplace.ViewPlaceActivity
import appjpm4everyone.utils.Common
import appjpm4everyone.utils.PermissionRequester
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Response

class MainActivity : BaseActivity(), OnMapReadyCallback,
    GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMyLocationClickListener {
    //Implements DataBinding
    private lateinit var binding: ActivityMainBinding

    //Implements Google Maps
    private lateinit var maps: GoogleMap

    //Permissions
    private val permissionRequest = PermissionRequester(this)

    // inside a basic activity
    private var locationManager: LocationManager? = null
    private var currentLat : Double = 0.0
    private var currentLng : Double = 0.0
    private var isFirstTime: Boolean = false


    //Retrofit service
    private lateinit var mService: IGoogleAPIService
    internal var currentPlaces: MyPlaces? = null
    private  val ZERO_RESULTS = "ZERO_RESULTS"
    private var placeSelected : String = "atm"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //To use DataBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Create persistent LocationManager reference
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?

        createMapFragment()
        initSpinner()
        initRetrofitClient()
        initUI()
    }

    private fun initUI() {
        binding.btnFind.setOnClickListener {
            nearByPlace(placeSelected)
            progressBar.show(this)
        }
    }

    private fun nearByPlace(typePlace: String) {
        //Clear all maker into map
        maps.clear()
        //build URL request base on location
        val url = getUrl(currentLat, currentLng, typePlace)

        //Retrofit services
        mService.getNearbyPlaces(url)
            .enqueue(object : retrofit2.Callback<MyPlaces>{
                override fun onResponse(call: Call<MyPlaces>?, response: Response<MyPlaces>?) {
                    //Get body of response
                    currentPlaces = response!!.body()

                    //Hide progress bar
                    progressBar.hideProgress()


                    if(response.isSuccessful){

                        if(response.body()!!.results.indices.isEmpty() || response.body()!!.status == "ZERO_RESULTS"){
                            //GeoJSON response
                            /*{
                                "html_attributions" : [],
                                "results" : [],
                                "status" : "ZERO_RESULTS"
                            }*/
                            showSnakyBar(getString(R.string.no_location))
                        }else {
                            for (i in response.body()!!.results.indices) {

                                showSnakyBar(getString(R.string.yes_location, placeSelected))
                                val marketOptions = MarkerOptions()
                                val googlePlace = response.body()!!.results[i]
                                val lat = googlePlace.geometry.location.lat
                                val lng = googlePlace.geometry.location.lng
                                val placeName = googlePlace.name

                                val latLng = LatLng(lat, lng)
                                marketOptions.position(latLng)
                                marketOptions.title(placeName)

                                //Find a hospitals
                                /*if(typePlace == "hospital"){
                                marketOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_hospital))
                            }else if(typePlace == "bank"){
                                marketOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_bank_24))
                            }else if(typePlace == "restaurant"){
                                marketOptions.icon(Utils.generateBitmapDescriptorFromRes(baseContext, R.drawable.open))
                                //marketOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant_24))
                            }else if(typePlace == "school"){
                                marketOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_school))
                            }else{
                                marketOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                            }*/

                                //Assign index for market
                                marketOptions.snippet(i.toString())

                                //Add market into map
                                maps.addMarker(marketOptions)
                                //move camera
                                /*maps.moveCamera(CameraUpdateFactory.newLatLng(latLng))
                                maps.animateCamera(CameraUpdateFactory.zoomTo(17f))*/
                            }
                        }

                    }
                }

                override fun onFailure(call: Call<MyPlaces>, t: Throwable) {
                    //Hide progress bar
                    progressBar.hideProgress()
                    showSnakyBar(t.message!!)
                }

            })

    }

    private fun showSnakyBar(message: String) {
        showLongSnackError(this, message)
    }

    private fun getUrl(currentLat: Double, currentLng: Double, typePlace: String): String {
        val googleMapPlaceUrl = StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
        googleMapPlaceUrl.append("?location=$currentLat,$currentLng")
        //googleMapPlaceUrl.append("?location=40.2984671,-3.4329859")
        googleMapPlaceUrl.append("&radius=15000") // 10 Km
        googleMapPlaceUrl.append("&type=$typePlace")
        //googleMapPlaceUrl.append("&key=AIzaSyDJwSEcfq3ijM50mbd1axRb4uBJWR6vZAo")
        googleMapPlaceUrl.append("&keyword=cruise&key=AIzaSyDJwSEcfq3ijM50mbd1axRb4uBJWR6vZAo")
        Log.d("Url DEBUG", googleMapPlaceUrl.toString())
        //return googleMapPlaceUrl.toString()
        //val example = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=4.686487,-74.054244&radius=10000&type=hospital&keyword=cruise&key=AIzaSyDJwSEcfq3ijM50mbd1axRb4uBJWR6vZAo"
        val example = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=10000&type=restaurant&keyword=cruise&key=AIzaSyDJwSEcfq3ijM50mbd1axRb4uBJWR6vZAo"
        Log.d("Url Example", example)
        return googleMapPlaceUrl.toString()
    }

    private fun initRetrofitClient() {
        mService = Common.googleAPIService
    }

    private fun initSpinner() {
        /*val places: MutableList<String> = mutableListOf()
        places.add("atm")
        places.add("bank")
        places.add("hospital")
        places.add("school")
        places.add("restaurant")*/

        val optionsPlace = getPlaces()
        val dataAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, R.layout.spinner_style, optionsPlace)
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.custom_spinner)
        binding.spType.adapter = dataAdapter
        binding.spType.setSelection(0)

        binding.spType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                placeSelected = p0?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                //"Not yet implemented"
            }

        }


    }

    //To easily test
    private fun getPlaces(): Array<String?> {
        return applicationContext.resources.getStringArray(R.array.place_options)
    }

    private fun createMapFragment() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.my_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        //Avoid nulls, only works if maps was initialized
        if (!::maps.isInitialized) return
        permissionRequest.additionalPermissions {
            maps.isMyLocationEnabled = true
            getLocation()
            //setLocation()
        }
    }

    private fun getLocation() {
        if(isPermissionsGranted()) {
            try {
                // Request location updates
                locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, locationListener)
                } catch(ex: SecurityException) {
                Log.d("myTag", "Security Exception, no location available")
            }
        }
    }

    //Callback from Maps
    override fun onMapReady(googleMap: GoogleMap) {
        //Constructor
        this.maps = googleMap
        createMarker()
        //Listeners summoned
        maps.setOnMyLocationButtonClickListener(this)
        maps.setOnMyLocationClickListener(this)
        enableMyLocation()

        //When you click into marker
        maps.setOnMarkerClickListener { marker ->
            //When user select market, just get result of place assign to static variable
            Common.currentResult = currentPlaces!!.results[(Integer.parseInt(marker.snippet))]
            //Start new activity
            startActivity(Intent(this@MainActivity, ViewPlaceActivity::class.java))
            true
        }
    }

    private fun createMarker() {
        val myHouse = LatLng(4.590841, -74.174112)
        val marker = MarkerOptions().position(myHouse).title("Mi casa")
        maps.addMarker(marker)
        //animation to zoom my favorite place
        /*maps.animateCamera(
            CameraUpdateFactory.newLatLngZoom(myHouse, 18f),
            6000,
            null
        )*/
    }

    //Avoid crash's app when the app is closed, deleted or cache data was erased
    private fun isPermissionsGranted() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    override fun onResumeFragments() {
        super.onResumeFragments()
        if (!::maps.isInitialized) return
        if (!isPermissionsGranted()) {
            maps.isMyLocationEnabled = false
            Toast.makeText(
                this,
                "Para activar la localización ve a ajustes y acepta los permisos",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "Boton Pulsado", Toast.LENGTH_SHORT).show()
        //It is false, because you need to locate it
        return false
    }

    override fun onMyLocationClick(p0: Location) {
        //simple constructor
        currentLat = p0.latitude
        currentLng = p0.longitude
        Toast.makeText(this, "Estás en ${p0.latitude}, ${p0.longitude}", Toast.LENGTH_SHORT).show()
    }

    //define the listener
    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            //thetext.text = ("" + location.longitude + ":" + location.latitude)
            //animation to zoom my favorite place
            if(isFirstTime) {
                val myHouse = LatLng(location.latitude, location.longitude)
                maps.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(myHouse, 18f),
                    6000,
                    null
                )
                isFirstTime=true
            }
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }
}