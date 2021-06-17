package com.apm2021.rankcity

import android.Manifest
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    var enable_ubication = false
    var firstLocation = true
    var punctuation = 0
    lateinit var chronometer: Chronometer
    var pauseOffSet: Long = 0
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var routeName: String
    var isTracking = true
    var addresses = mutableListOf<String>()
    var addresses_score = JSONArray()
    private lateinit var bitmap: Bitmap
    private lateinit var file: File

    private lateinit var currentDate: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        theme.applyStyle(R.style.primaryColors, true)
        setContentView(R.layout.activity_maps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")
        currentDate = sdf.format(Date())
        chronometer = findViewById(R.id.chronometer)

        //startChronometer()
        findViewById<TextView>(R.id.punctuationText).text = punctuation.toString()
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        startLocationTracking(isTracking)
        startChronometer()
        startService()

        // Pulsar boton stop nos lleva a InfoActivity
        val stopButton = findViewById<Button>(R.id.stopRouteButton) as Button
        // set on-click listener
        stopButton.setOnClickListener {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
            stopChronometer()
            stopButton.visibility = View.GONE
            snapShot()
            titleRouteDialog()
            stopService()
        }

    }

    override fun onRestart() {
        super.onRestart()
        val stopButton = findViewById<Button>(R.id.stopRouteButton) as Button
        stopButton.visibility = View.VISIBLE
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

    }

    private fun checkPermissions() =
        ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED

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
                for (i in 0..returnedAddress.maxAddressLineIndex) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n")
                }
                strAdd = strReturnedAddress.toString().split(",")[0]
                Log.w("Current loction address", strAdd)
            } else {
                Log.w("Current loction address", "No Address returned!")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            strAdd = "Invalid address"
            Log.w("Current loction address", "Canont get Address!")
        }
        return strAdd
    }

    private fun increasePunctuation(currentAddress: String, addresses: List<String>){
        val addressesSize = addresses.size
        if (currentAddress !in addresses && currentAddress != null){
            punctuation += 100
            findViewById<TextView>(R.id.punctuationText).text = punctuation.toString()
            val json = JSONObject()
            json.put("name", currentAddress)
            json.put("score", 100)
            addresses_score.put(json)
        }
        else if (addressesSize >= 2 && currentAddress !in addresses.subList(addressesSize-2, addressesSize)){
            punctuation += 50
            findViewById<TextView>(R.id.punctuationText).text = punctuation.toString()
            val json = JSONObject()
            json.put("name", currentAddress)
            json.put("score", 50)
            addresses_score.put(json)
        }
    }

    private fun startChronometer() {
        chronometer.base = SystemClock.elapsedRealtime() - pauseOffSet
        chronometer.start()
    }

    private fun stopChronometer() {
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

        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    private fun update(){
        val latLngs = mutableListOf<LatLng>()
        var currentAddress: String
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

                    if (firstLocation){
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 16f))
                        firstLocation = false
                    }

                    //Add new location to the list
                    latLngs.add(location)
                    //Get the name of the street
                    currentAddress = getCompleteAddressString(location.latitude, location.longitude)
                    //Print the route
                    printPolyline(latLngs)
                    //Increase punctuation depending on the street
                    if(currentAddress != "Invalid address") {
                        increasePunctuation(currentAddress, addresses)
                        if (addresses.size == 0 || currentAddress != addresses[addresses.size-1]) {
                            addresses.add(currentAddress)
                        }
                    }
                    Log.w("Direccion actual ", currentAddress)
                    Log.w("Array ", addresses.last().toString())
                    Log.w("Punctuation ", punctuation.toString())
                }
            }
        }
    }

    private fun titleRouteDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setTitle("Dale un título a la ruta")

        // Set up the input
        val input = EditText(this)
        // Specify the type of input expected
        input.setHint("Enter Text")
        builder.setView(input)


                // Set up the buttons
                builder.setPositiveButton("OK") { dialog, i ->
                    // Here you get get input text from the Edittext
                    routeName = input.text.toString()
                    if (routeName != "") {
                        val intent = Intent(this, InfoActivity::class.java).apply {
                            putExtra("routeName", routeName)
                            putExtra("punctuation", punctuation)
                            putExtra("time", chronometer.text)
                            putExtra("currentDate", currentDate)
                            putExtra("addresses_score", addresses_score.toString())
                            putExtra("file", file)
                        }

                        startActivity(intent)
                    }else{
                        titleRouteDialog()
                        Toast.makeText(this, "Debes darle un título a la ruta", Toast.LENGTH_SHORT).show()
                    }
                }

                builder.setNegativeButton("Cancel") {
                        dialog, which -> dialog.cancel()
                        val stopButton = findViewById<Button>(R.id.stopRouteButton) as Button
                        stopButton.visibility = View.VISIBLE
                        startChronometer()
                        startLocationTracking(isTracking)
                }


        builder.show()
    }

    override fun onBackPressed() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        stopChronometer()
        stopService()
        exitDialog()
    }

    private fun exitDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        builder.setTitle("¿Estás seguro de que quieres salir de la ruta?")

        // Set up the buttons
        builder.setPositiveButton("Sí") { dialog, i ->
            finish()
        }

        builder.setNegativeButton("No") {
                dialog, which -> dialog.cancel()
            startChronometer()
            startLocationTracking(isTracking)
            startService()
        }
        builder.show()
    }

    fun Bitmap.toByteArray():ByteArray{
        ByteArrayOutputStream().apply {
            compress(Bitmap.CompressFormat.JPEG,10,this)
            return toByteArray()
        }
    }

    private fun snapShot(){
        val callback =
            SnapshotReadyCallback { snapshot ->
                if (snapshot != null) {
                    bitmap = snapshot
                }
                try {
                    file = File(getExternalFilesDir(null)?.canonicalPath, "map.png")
                    val fout = FileOutputStream(file)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, fout)
                    fout.flush()
                    fout.close()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        mMap.snapshot(callback)
    }

    fun startService() {
        val serviceIntent = Intent(this, RouteService::class.java)
        serviceIntent.putExtra("inputExtra", "Route Service")
        serviceIntent.putExtra("chronometer", chronometer.base)
        ContextCompat.startForegroundService(this, serviceIntent)
    }
    fun stopService() {
        val serviceIntent = Intent(this, RouteService::class.java)
        stopService(serviceIntent)
    }


}