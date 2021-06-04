package com.apm2021.rankcity

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Chronometer
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


typealias Polyline = MutableList<LatLng>

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    val REQUEST_PERMISSIONS_REQUEST_CODE = 1234
    var enable_ubication = false
    var punctuation = 0
    private lateinit var chronometer: Chronometer
    var pauseOffSet: Long = 0
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var currentLocation: Location? = null

/**var isFirstRun = true
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    companion object {
        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<Polylines>()
    }

    private fun postInitialValues(){
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        theme.applyStyle(R.style.primaryColors, true)
        setContentView(R.layout.activity_maps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        chronometer = findViewById(R.id.chronometer)
        startChronometer()
        findViewById<TextView>(R.id.punctuationText).text = punctuation.toString()
        fusedLocationProviderClient = FusedLocationProviderClient(this)
/**postInitialValues()
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        isTracking.observe(this, Observer {
            updateLocationTracking(it)
        })*/

        // Pulsar boton stop nos lleva a InfoActivity
        val stopButton = findViewById<Button>(R.id.stopRouteButton) as FloatingActionButton;
        // set on-click listener
        stopButton.setOnClickListener {
            stopChronometer()
            GlobalScope.launch {
                println("Llamada API, POST para añadir nueva ruta")
                /**val conn = URL("http://localhost:5000/routes").openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.connectTimeout = 300000
                conn.doOutput = true
                val message = " {\n" +
                "                \"route\": " + routeid +",\n" +
                "                \"title\": " + title +",\n" +
                "                \"date\": " + date +"\n" +
                "                \"user\": " + userid +",\n" +
                "                \"score\": " + score +",\n" +
                "            }"*/
            }
            Toast.makeText(this, "Route finished", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, InfoActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()

        if (!checkPermissions()) {
            requestPermissions()
        } else {
            startLocationTracking(true)
            update()
            /**getLocation()*/
        }
    }

    /**fun getLocation() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val latLngs = listOf<LatLng>()

        if (gpsStatus) {
            val fusedLocation = LocationServices.getFusedLocationProviderClient(applicationContext)
            try {
                val locationResult = fusedLocation.lastLocation
                locationResult.addOnCompleteListener(this, OnCompleteListener<Location> {
                    if (it.isSuccessful) {
                        if (it.result == null) {
                            getLocation()
                        } else if (mMap == null) {
                            enable_ubication = true
                        } else {
                            val location = LatLng(
                                it.result!!.latitude,
                                it.result!!.longitude
                            )

                            latLngs.toMutableList().add(location)

                            getCompleteAddressString(location.latitude, location.longitude)
                            mMap.isMyLocationEnabled = true
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 17f))
                            printPolyline(latLngs)
                        }
                    }
                })
            } catch (e: SecurityException) {
                Log.e("Exception: %s", e.message!!)
            }
        } else {
            Snackbar.make(
                findViewById(R.id.maps_layout), "Activa tu ubicación!",
                Snackbar.LENGTH_INDEFINITE
            ).setAction(
                "La he activado",
                View.OnClickListener { getLocation() }).show()
        }
    }*/

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSIONS_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
                    // Crear peticion de ubicacion
                    /**getLocation()*/
                    startLocationTracking(true)
                    update()
                } else {
                    Snackbar.make(
                        findViewById(R.id.maps_layout), "QUIERO PERMISOS, DAME PERMISOS!!!",
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction(
                        "Permitir",
                        View.OnClickListener { startLocationPermissionRequest() }).show()
                }
            }
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true

//        Add in future iterations markers for favourites streets
        val coruna = LatLng(43.371926604109944, -8.403665934971855)
        mMap.addMarker(MarkerOptions().position(coruna).title("A Coruña").snippet("Playa de Orzán"))

        if (checkPermissions() && enable_ubication) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    ACCESS_FINE_LOCATION
                ) != PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            mMap.isMyLocationEnabled = true
        }

//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user, 15.8F))
    }

    private fun checkPermissions() =
        ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED

    private fun startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(ACCESS_FINE_LOCATION),
            REQUEST_PERMISSIONS_REQUEST_CODE
        )
    }

    private fun requestPermissions() {
        // Comprueba si lo pediste alguna vez
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, ACCESS_FINE_LOCATION)) {
            Snackbar.make(
                findViewById(R.id.maps_layout), "QUIERO PERMISOS, DAME PERMISOS!!!",
                Snackbar.LENGTH_INDEFINITE
            ).setAction("Permitir", View.OnClickListener { startLocationPermissionRequest() })
                .show()
        } else {
            startLocationPermissionRequest()
        }
    }

    private fun printPolyline(latLngs: List<LatLng>){
        val polylineOptions = PolylineOptions()

        for (location in latLngs) {
            polylineOptions.add(location)
                .width(13f)
                .color(ContextCompat.getColor(this, R.color.button_primary))
        }
        val polyline = mMap.addPolyline(polylineOptions)
        polyline.startCap = RoundCap()
    }

    private fun getCompleteAddressString(LATITUDE: Double, LONGITUDE: Double): String {
        var strAdd = ""
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1)
            if (addresses != null) {
                val returnedAddress: Address = addresses[0]
                val strReturnedAddress = StringBuilder("")
                for (i in 0..returnedAddress.getMaxAddressLineIndex()) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n")
                }
                strAdd = strReturnedAddress.toString().split(",")[0]
                Toast.makeText(this, strAdd, Toast.LENGTH_SHORT).show()
                Log.w("Current loction address", strAdd)
            } else {
                Log.w("Current loction address", "No Address returned!")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.w("Current loction address", "Canont get Address!")
        }
        return strAdd
    }

    private fun increasePunctuation(currentAddress: String, addresses: List<String>){
        if (currentAddress !in addresses && currentAddress != null){
            punctuation += 100
            findViewById<TextView>(R.id.punctuationText).text = punctuation.toString()
        }
    }

    fun startChronometer() {
        chronometer.base = SystemClock.elapsedRealtime() - pauseOffSet
        chronometer.start()
    }

    fun stopChronometer() {
        chronometer.stop()
        pauseOffSet = SystemClock.elapsedRealtime() - chronometer.base
    }

    @SuppressLint("MissingPermission")
    private fun startLocationTracking(isTracking: Boolean){
        if(isTracking){
            val request = LocationRequest().apply {
                interval = 5000L
                fastestInterval = 2000L
                priority = PRIORITY_HIGH_ACCURACY
            }

            update()

            fusedLocationProviderClient.requestLocationUpdates(
                request, locationCallback, Looper.getMainLooper()
            )
            Toast.makeText(this, "Entra", Toast.LENGTH_SHORT).show()

        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    private fun update(){
        val latLngs = mutableListOf<LatLng>()
        var currentAddress: String = "Isaac Peral"
        var addresses = mutableListOf<String>()
        locationCallback = object : LocationCallback() {
            @SuppressLint("MissingPermission")
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                for (locations in locationResult.locations){
                    val location = LatLng(
                        locations.latitude,
                        locations.longitude
                    )
                    mMap.isMyLocationEnabled = true
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 17f))

                    //Add new location to the list
                    latLngs.add(location)
                    //Get the name of the street
                    currentAddress = getCompleteAddressString(location.latitude, location.longitude)
                    //Print the route
                    printPolyline(latLngs)
                    //Increase punctuation depending on the street
                    increasePunctuation(currentAddress, addresses)
                    addresses.add(currentAddress)
                    Log.w("Direccion actual ", currentAddress)
                    Log.w("Array ", addresses.last().toString())
                    Log.w("Punctuation ", punctuation.toString())
                }
            }
        }
    }


/**private fun addEmptyPolyline() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))

    val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            if(isTracking.value!!) {
                result?.locations?.let { locations ->
                    for(location in locations) {
                        addPathPoint(location)
                    }
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean){
        if(isTracking){
            val request = LocationRequest().apply {
                interval = 5000L
                fastestInterval = 2000L
                priority = PRIORITY_HIGH_ACCURACY
            }
            fusedLocationProviderClient.requestLocationUpdates(
                request, locationCallback, Looper.getMainLooper()
                )

        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    private fun addPathPoint(location: Location?){
        location?.let {
            val pos = LatLng(location.latitude, location.longitude)
            pathPoints.value?.apply {
                last().add(pos)
                pathPoints.postValue(this)
            }
        }
    }

    private fun startForegroundService() {
        addEmptyPolyline()
        isTracking.postValue(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
    }*/
}