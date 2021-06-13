package com.apm2021.rankcity.ui.leaderboard

import android.content.Context
import android.content.SharedPreferences
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.apm2021.rankcity.R
import com.google.gson.GsonBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class Datasource_Leaderboard(val context: LeaderboardFragment) {
    val ranking_list = mutableListOf<JSONObject>()
    fun getUsersList(): List<String> {
        GlobalScope.launch {
            getRankingFromAPI()
        }
        val ranking = listOf<String>("HOLA", "CHAO")
        return ranking
    }

    private fun getRankingFromAPI() = runBlocking {
        val requestQueue = Volley.newRequestQueue(context.activity)
        val url = "http://192.168.1.58:5000/ranking"
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                println("Ranking"+response)
                try {
                    for (i in 0 until response.length()) {
                        val user: JSONObject = response.getJSONObject(i)
                        ranking_list.add(user)
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
