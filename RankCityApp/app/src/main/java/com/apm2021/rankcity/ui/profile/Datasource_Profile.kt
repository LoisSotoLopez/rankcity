package com.apm2021.rankcity.ui.profile

import android.content.Context
import android.content.SharedPreferences
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.apm2021.rankcity.R
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class Datasource_Profile(val context: ProfileFragment) {
    //    ESTO ES PARA LAS RUTAS DEL PERFIL
    var routes = mutableListOf<JSONObject>()
    fun getDatesList(): Array<String> {
//        val result = ArrayList<String>()
        var userid = String()
        val sharedPreferences: SharedPreferences? =
            context.activity?.getSharedPreferences("user_data_file", Context.MODE_PRIVATE)
        if (sharedPreferences != null) {
            userid = sharedPreferences.getString("userId","").toString()
        }
        GlobalScope.launch {
            getUserRoutesFrom_API(userid)
        }
//        result.add(userid)
        return context.resources.getStringArray(R.array.dates_array)
    }

    private fun getUserRoutesFrom_API(userid: String) = runBlocking {
        val requestQueue = Volley.newRequestQueue(context.activity)
        val url = "http://192.168.1.58:5000/routes/user/$userid"
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                println("RUTAS"+response)
                try {
                    for (i in 0 until response.length()) {
                        val route: JSONObject = response.getJSONObject(i)
                        routes.add(route)
                    }
                } catch (e: Exception) {

                }
            },
            { error ->
                // TODO: Handle error
                println("ERROR API CONECCTION")
            }
        )
        requestQueue.add(jsonArrayRequest)
    }
}
