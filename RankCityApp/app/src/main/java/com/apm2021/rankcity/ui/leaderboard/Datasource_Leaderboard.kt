package com.apm2021.rankcity.ui.leaderboard

import android.content.Context
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.apm2021.rankcity.R
import com.google.gson.GsonBuilder
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

//class Datasource_Leaderboard(val context: LeaderboardFragment) {
//    fun getUsersList(): Array<String> {
//
//        // Return users list from string resources
//        return context.resources.getStringArray(R.array.users_array)
//    }
//}

class Datasource_Leaderboard(val context: LeaderboardFragment) {
    fun getUsersList(): List<String> {
        // API GET ALL USERS
//        val url = "http://192.168.1.58:5000/ranking"
//        val jsonObjectRequest = JsonArrayRequest(Request.Method.GET, url, null,
//            { response ->
////                textView.text = "Response: %s".format(response.toString())
//                ranking_list = response
//            },
//            { error ->
//                // TODO: Handle error
//            }
//        )
//        val ranking_list = getRankingFromAPI()
        val ranking_list = listOf<String>("HOLA", "CHAO")

        return ranking_list
    }

//    private fun getRankingFromAPI(): List<String> {

//        val url = URL("http://192.168.1.58:5000/ranking")
//        val con = url.openConnection() as HttpURLConnection
//        con.connectTimeout = 30000
//        con.requestMethod = "GET"
//        con.setRequestProperty("Content-Type", "application/json")
//        con.setRequestProperty("Accept", "application/json")
//        con.setRequestProperty("Accept-Charset", "utf-8,*")
//        try {
//            val postData: ByteArray = message.toByteArray(StandardCharsets.UTF_8)
//
//            val outputStream: DataOutputStream = DataOutputStream(connection.outputStream)
//            outputStream.write(postData)
//            outputStream.flush()
////            val inputAsString = con.inputStream.bufferedReader().use { it.readText() }
//            val list = listOf<String>("HOLA", "CHAO")
//            return list
//
//        } finally {
//            con.disconnect()
//        }
//    }
}