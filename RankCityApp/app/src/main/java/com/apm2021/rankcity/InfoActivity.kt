package com.apm2021.rankcity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class InfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        theme.applyStyle(R.style.primaryColors, true)
        setContentView(R.layout.activity_info)

        // Pulsar boton home nos lleva al inicio de la app
        val homeButton = findViewById<Button>(R.id.buttonBackHome);
        // set on-click listener
        homeButton.setOnClickListener {
            Toast.makeText(this, "Return to home", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val shareButton = findViewById<Button>(R.id.buttonShare);
        // set on-click listener
        shareButton.setOnClickListener {
            Toast.makeText(this, "Share route", Toast.LENGTH_SHORT).show()
        }
    }
}