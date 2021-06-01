package com.apm2021.rankcity

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
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
typealias Polylines = MutableList<Polyline>

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    val REQUEST_PERMISSIONS_REQUEST_CODE = 1234
    var enable_ubication = false
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
        setContentView(R.layout.activity_maps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

/**postInitialValues()
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        isTracking.observe(this, Observer {
            updateLocationTracking(it)
        })*/

        // Pulsar boton stop nos lleva a InfoActivity
        val stopButton = findViewById<Button>(R.id.stopRouteButton) as FloatingActionButton;
        // set on-click listener
        stopButton.setOnClickListener {
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
            getLocation()
        }
    }

    fun getLocation() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

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
                            getCompleteAddressString(location.latitude, location.longitude)
                            mMap.isMyLocationEnabled = true
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 17f))
                            printPolyline()
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
    }

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
                    getLocation()
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

    private fun getCompleteAddressString(LATITUDE: Double, LONGITUDE: Double): String? {
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
                strAdd = strReturnedAddress.toString()
                Log.w("Current loction address", strReturnedAddress.toString())
            } else {
                Log.w("Current loction address", "No Address returned!")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.w("Current loction address", "Canont get Address!")
        }
        return strAdd
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

    private fun printPolyline(){
        val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                ACCESS_FINE_LOCATION
            ) != PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                val location2 = location?.let {
                    LatLng(
                        it.latitude,
                        it.longitude
                    )
                }

                val polylineOptions = PolylineOptions().add(location2)
                    .add(LatLng(43.357426851502325, -8.420553803443909))
                    .add(LatLng(43.358203023732095, -8.421143889427185))
                    .add(LatLng(43.35844094388473, -8.421149253845215))
                    .add(LatLng(43.35891288109901, -8.420151472091675))
                    .add(LatLng(43.35902598947281, -8.419818878173828))
                    .add(LatLng(43.35798850476193, -8.419067859649656))
                    .add(LatLng(43.358815373710975, -8.418735265731812))
                    .width(13f)
                    .color(ContextCompat.getColor(this, R.color.button_primary))
                val polyline = mMap.addPolyline(polylineOptions)
                polyline.startCap = RoundCap()
                polyline.endCap = CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.arrow))
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