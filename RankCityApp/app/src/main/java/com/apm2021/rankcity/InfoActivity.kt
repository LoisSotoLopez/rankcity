package com.apm2021.rankcity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject


class InfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        theme.applyStyle(R.style.primaryColors, true)
        setContentView(R.layout.activity_info)

        var bundle = intent.extras
        val routeName = bundle?.getString("routeName")
//        val punctuation = bundle?.getString("punctuation")
        val punctuation = bundle?.getInt("punctuation")
        val time = bundle?.getString("time")
        val currentDate = bundle?.getString("currentDate")
        //val byteArray = bundle?.getByteArray("byteArray")
        //val screenshot = intent.getParcelableExtra<Parcelable>("screenshot") as Bitmap?
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences("user_data_file", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userId","").toString()
//        val data = JSONObject("""{"name":"caxe", "score":25}""")
//        val data2 = JSONObject("""{"name":"caxe2", "score":35}""")
//        val streets= JSONArray(listOf(data, data2))
        val streets =  JSONArray(getIntent().getStringExtra("addresses_score"));
        GlobalScope.launch {
            addRouteAPI(userId, routeName, currentDate, time, punctuation, streets)
        }

        findViewById<TextView>(R.id.routeName).text = routeName
        findViewById<TextView>(R.id.punctuation).text = punctuation.toString()
        findViewById<TextView>(R.id.time).text = time
        //findViewById<ImageView>(R.id.screenshot).setImageBitmap(byteArray?.toBitmap())


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
            Toast.makeText(this, "Share route", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addRouteAPI(userId: String, title: String?, date: String?, time: String?, score: Int?, streets: JSONArray) {
        // Instantiate the RequestQueue.
        val queue = Volley.newRequestQueue(this)
        val url = "http://192.168.1.58:5000/routes/user/"+userId

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

//    private fun addStreetAPI(currentAddress: String?) {
//        // Instantiate the RequestQueue.
//        val queue = Volley.newRequestQueue(this)
//        val url = "http://192.168.1.58:5000/streets"
//
//        // TODO generar random ids
//        val jsonObject = JSONObject()
//        jsonObject.put("name", currentAddress)
//
//        val jsonRequest = JsonObjectRequest(url, jsonObject, {}, {})
//        // Add the request to the RequestQueue.
//        queue.add(jsonRequest)
//    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    override fun onStop() {
        super.onStop()
        finish()
    }

    /**fun ByteArray.toBitmap():Bitmap{
    return BitmapFactory.decodeByteArray(this,0,size)
    }*/
}