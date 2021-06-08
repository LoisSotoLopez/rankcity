package com.apm2021.rankcity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class InfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        theme.applyStyle(R.style.primaryColors, true)
        setContentView(R.layout.activity_info)

        var bundle = intent.extras
        val routeName = bundle?.getString("routeName")
        val punctuation = bundle?.getString("punctuation")
        val time = bundle?.getString("time")

        findViewById<TextView>(R.id.routeName).text = routeName
        findViewById<TextView>(R.id.punctuation).text = punctuation
        findViewById<TextView>(R.id.time).text = time


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

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onStop() {
        super.onStop()
        finish()
    }
}