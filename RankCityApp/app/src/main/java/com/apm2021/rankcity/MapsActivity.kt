package com.apm2021.rankcity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

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
            Toast.makeText(this, "Route finished", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, InfoActivity::class.java)
            startActivity(intent)
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
        mMap.addMarker(MarkerOptions().position(user).icon(BitmapDescriptorFactory.fromResource(R.drawable.arrow)))


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user, 15.8F))
    }
}