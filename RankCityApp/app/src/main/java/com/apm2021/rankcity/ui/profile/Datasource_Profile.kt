package com.apm2021.rankcity.ui.profile

import android.content.Context
import android.content.SharedPreferences
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.apm2021.rankcity.R

class Datasource_Profile(val context: ProfileFragment) {
//    ESTO ES PARA LAS RUTAS DEL PERFIL
    fun getDatesList(): Array<String> {
//        val result = ArrayList<String>()
        var userid = String()
        val sharedPreferences: SharedPreferences? =
            context.activity?.getSharedPreferences("user_data_file", Context.MODE_PRIVATE)
        if (sharedPreferences != null) {
            userid = sharedPreferences.getString("userId","").toString()
        }
//        result.add(userid)
        return context.resources.getStringArray(R.array.dates_array)
//        return result.toArray()
//        return arrayOf<String>("uno", "dos")
    }

//    private fun getUserRoutesFrom_API(userid: String) {
//        val requestQueue = Volley.newRequestQueue(context.activity)
//        val url = "http://192.168.1.58:5000/routes/user/$userid"
//        val jsonObjectRequest = JsonObjectRequest(
//            Request.Method.GET, url, null,
//            { response ->
////                println("RUUUUUUUUUUUUUUUUUTAS"+response)
//                val sharedPreferences: SharedPreferences? =
//                    context.activity?.getSharedPreferences("user_routes_file", Context.MODE_PRIVATE)
//                val editor: SharedPreferences.Editor = sharedPreferences?.edit() ?:
//                    editor.putString("userId", response.getString("username"))
//                    editor.apply()
//                    editor.commit()
////                println(sharedPreferences.getString("userId", "holahola"))
//            },
//            { error ->
//                // TODO: Handle error
//                println("ERROR API CONECCTION")
//            }
//        )
//        requestQueue.add(jsonObjectRequest)
//    }
}