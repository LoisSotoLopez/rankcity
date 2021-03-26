package com.apm2021.rankcity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Pulsar boton login nos lleva al inicio de la app
        val loginButton = findViewById<Button>(R.id.loginButton);
        // set on-click listener
        loginButton.setOnClickListener {
            Toast.makeText(this, "Login correct", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}