package com.apm2021.rankcity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

enum class ProviderType {
    BASIC,
    GOOGLE
}

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        theme.applyStyle(R.style.primaryColors, true)
        setContentView(R.layout.activity_main)

        //Saving auth data
        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?. getString("provider")

        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()
        //TODO: clear prefs entries for email and provider once app properly closes

        Toast.makeText(this, "Email: "+email+" Provider: "+provider, Toast.LENGTH_SHORT).show()

        // Llamada a la barra de navegacion
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_leaderboard, R.id.navigation_profile))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

    }

    override fun onBackPressed() {
        //super.onBackPressed()
        exitDialog()

    }

    private fun exitDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¿Estás seguro de que quieres salir de RankCity?")

        // Set up the buttons
        builder.setPositiveButton("Sí") { dialog, i ->
            finishAffinity()
        }

        builder.setNegativeButton("No") {
                dialog, which -> dialog.cancel()
        }

        builder.setNeutralButton("Cerrar sesión"){ dialog, i ->
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        builder.show()
    }
}