package com.apm2021.rankcity

import android.content.Context
import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.OutputStream
import java.net.URI


class InfoActivity : AppCompatActivity() {

    protected val scopeMap = CoroutineScope(
        Dispatchers.Main
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        theme.applyStyle(R.style.primaryColors, true)
        setContentView(R.layout.activity_info)

        var bundle = intent.extras
        val routeName = bundle?.getString("routeName")
        val punctuation = bundle?.getInt("punctuation")
        val time = bundle?.getString("time")
        val currentDate = bundle?.getString("currentDate")
        //val byteArray = bundle?.getByteArray("byteArray")
        //val screenshot = intent.getParcelableExtra<Parcelable>("screenshot") as Bitmap?
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences("user_data_file", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId","").toString()
        val streets =  JSONArray(getIntent().getStringExtra("addresses_score"));
        val byteArray = bundle?.getByteArray("byteArray")
        val bitmap = byteArray?.toBitmap()
        scopeMap.launch {
            addRouteAPI(userId, routeName, currentDate, time, punctuation, streets)
        }

        findViewById<TextView>(R.id.routeName).text = routeName
        findViewById<TextView>(R.id.punctuation).text = punctuation.toString()
        findViewById<TextView>(R.id.time).text = time
        findViewById<ImageView>(R.id.screenshot).setImageBitmap(bitmap)


        // Pulsar boton home nos lleva al inicio de la app
        val homeButton = findViewById<Button>(R.id.buttonBackHome)
        // set on-click listener
        homeButton.setOnClickListener {
            Toast.makeText(this, "Return to home", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val shareButton = findViewById<Button>(R.id.buttonShare)
        // set on-click listener
        shareButton.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.type = "image/jpeg"
            val uri = bitmap?.let { it1 -> getUri(it1) }
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.putExtra(Intent.EXTRA_TEXT, "Información de $routeName")
            val chooseIntent = Intent.createChooser(intent, "Compartir")
            startActivity(chooseIntent)
            Toast.makeText(this, "Share route", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addRouteAPI(userId: String, title: String?, date: String?, time: String?, score: Int?, streets: JSONArray) {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "https://rankcity-app.herokuapp.com/routes/user/"+userId
//        val url = "http://192.168.1.74:5000/routes/user/"+userId

        // TODO generar random ids
        val jsonObject = JSONObject()
        jsonObject.put("title", title)
        jsonObject.put("date", date)
        jsonObject.put("time", time)
        jsonObject.put("score", score)
        jsonObject.put("streets", streets)

        val jsonRequest = JsonObjectRequest(url, jsonObject, {}, {})
        // Add the request to the RequestQueue.
        queue.add(jsonRequest)
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

    fun ByteArray.toBitmap():Bitmap{
        return BitmapFactory.decodeByteArray(this,0,size)
    }

    private fun getUri(bitmap: Bitmap): Uri? {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "title")
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        val uri: Uri? = contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values
        )
        val outstream: OutputStream?
        try {
            outstream = uri?.let { contentResolver.openOutputStream(it) }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outstream)
            outstream?.close()
        } catch (e: Exception) {
            System.err.println(e.toString())
        }
        return uri
    }
}