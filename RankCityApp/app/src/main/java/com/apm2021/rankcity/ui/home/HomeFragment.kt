package com.apm2021.rankcity.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.apm2021.rankcity.MainActivity
import com.apm2021.rankcity.MapsActivity
import com.apm2021.rankcity.R
import com.google.android.material.dialog.MaterialDialogs


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        // Boton a empezar
        val startRouteButton = root.findViewById(R.id.period_change) as Button;
        startRouteButton.setOnClickListener {
            checkLocationPermission()
        }
        return root;
    }

    private fun switchActivities() {
        val intent = Intent(activity, MapsActivity::class.java)
        startActivity(intent)
    }

    private fun checkLocationPermission() {
        if (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            }
            != PackageManager.PERMISSION_GRANTED) {
            //El permiso no está aceptado.
            requestLocationPermission()

        } else {
            switchActivities()
        }
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            //El usuario ya ha rechazado el permiso anteriormente, debemos informarle que vaya a ajustes.
                // Por ahora seguiremos mostrando la notificación para dar la posibilidad de aceptar los permisos
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                777
            )
        } else {
            //El usuario nunca ha aceptado ni rechazado, así que le pedimos que acepte el permiso.
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                777
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 777) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                switchActivities()

            } else {
                Toast.makeText(context, "Permiso rechazado", Toast.LENGTH_SHORT).show()
            }

        }
    }
}