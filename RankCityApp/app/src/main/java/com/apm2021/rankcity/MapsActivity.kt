package com.apm2021.rankcity

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    val REQUEST_PERMISSIONS_REQUEST_CODE = 1234

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

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

//        println("ESTA ES MI LOCALIZACION$gpsStatus")
        if (gpsStatus) {

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

        val coruna = LatLng(43.371926604109944, -8.403665934971855)
        mMap.addMarker(MarkerOptions().position(coruna).title("A Coruña").snippet("Playa de Orzán"))

        val user = LatLng(43.36704038404932, -8.40577771078702)
//        mMap.addMarker(MarkerOptions().position(user).icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow)))


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user, 15.8F))
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
}