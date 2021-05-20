package com.apm2021.rankcity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Spanned
import android.text.method.ScrollingMovementMethod
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.google.android.material.snackbar.Snackbar
import java.io.InputStream


class EulaActivity : AppCompatActivity() {

    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = applicationContext
            .getSharedPreferences("com.apm2021.rankcity", Context.MODE_PRIVATE)
        setContentView(R.layout.activity_eula)
        initializeUI()
    }

    private fun initializeUI() {
        val eulaCancel = findViewById<Button>(R.id.eula_cancel)
        val eulaConfirm = findViewById<Button>(R.id.eula_confirm)
        val eulaContent = findViewById<TextView>(R.id.eula_content)

        eulaContent.setMovementMethod(ScrollingMovementMethod())
        eulaCancel.setOnClickListener { cancelEULA() }
        eulaConfirm.setOnClickListener {
            confirmEULA()
        }
        setEula()
    }

    private fun setEula() {
        val eulaContent = findViewById<TextView>(R.id.eula_content)
        val eula = intent.extras!!.getInt("eula")
        val inputStream: InputStream = resources.openRawResource(eula)
        val bytes = ByteArray(inputStream.available())
        inputStream.read(bytes)
        val htmlAsSpanned: Spanned =
            HtmlCompat.fromHtml(String(bytes), HtmlCompat.FROM_HTML_MODE_LEGACY)
        eulaContent.text = htmlAsSpanned
    }

    private fun cancelEULA() {
        Snackbar.make(
            findViewById(android.R.id.content),
            "You must accept the EULA to continue",
            Snackbar.LENGTH_LONG
        ).show()
    }

    // NOTE: Here you would call your api to save the status
    private fun confirmEULA() {
        preferences.edit().putBoolean("eulaAccepted", true).apply()
        val intent = Intent()
        setResult(RESULT_OK, intent)
        finish()
    }
}