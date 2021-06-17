package com.apm2021.rankcity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.File


class InfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        theme.applyStyle(R.style.primaryColors, true)
        setContentView(R.layout.activity_info)

        val bundle = intent.extras
        val routeName = bundle?.getString("routeName")
        val punctuation = bundle?.getString("punctuation")
        val time = bundle?.getString("time")
        val file = bundle?.getByteArray("file")

        findViewById<TextView>(R.id.routeName).text = routeName
        findViewById<TextView>(R.id.punctuation).text = punctuation
        findViewById<TextView>(R.id.time).text = time
        findViewById<ImageView>(R.id.screenshot).setImageBitmap(file?.toBitmap())


        // Pulsar boton home nos lleva al inicio de la app
        val homeButton = findViewById<Button>(R.id.buttonBackHome)
        // set on-click listener
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val shareButton = findViewById<Button>(R.id.buttonShare)
        // set on-click listener
        shareButton.setOnClickListener {
            getUri(routeName)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }


    fun ByteArray.toBitmap():Bitmap{
        return BitmapFactory.decodeByteArray(this,0,size)
    }

    private fun getUri(routeName: String?) {
        val fileSource = File(getExternalFilesDir(null)?.canonicalPath, "map.png")

        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "image/png"
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(fileSource.absolutePath))
        intent.putExtra(Intent.EXTRA_TEXT, routeName)
        startActivity(Intent.createChooser(intent, "Compartir ruta"))
    }
}