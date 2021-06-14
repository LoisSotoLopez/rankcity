package com.apm2021.rankcity.ui.leaderboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.apm2021.rankcity.R
import com.google.gson.*
import kotlinx.coroutines.runBlocking
import java.lang.reflect.Type

class LeaderboardFragment : Fragment() {

    private lateinit var leaderboardViewModel: LeaderboardViewModel
    private var mranking = MutableLiveData<ArrayList<UserRanking>>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_leaderboard, container, false)
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)
        getRankingFrom_API()

//        val usersList = Datasource_Leaderboard(this).getUsersList()

        val recyclerView = itemView.findViewById<RecyclerView>(R.id.recycler_view)
        mranking.observe(viewLifecycleOwner,{
//            Toast.makeText(context, it[0].toString(), Toast.LENGTH_SHORT).show()
            recyclerView.apply {
                // set a LinearLayoutManager to handle Android
                // RecyclerView behavior
                layoutManager = LinearLayoutManager(activity)
                // set the custom adapter to the RecyclerView
                adapter = LeaderboardAdapter(it)
            }
        })
//        //GlobalScope.launch {
//            recyclerView.apply {
//                // set a LinearLayoutManager to handle Android
//                // RecyclerView behavior
//                layoutManager = LinearLayoutManager(activity)
//                // set the custom adapter to the RecyclerView
//                adapter = LeaderboardAdapter(usersList)
//            }
        //}
    }

    private fun getRankingFrom_API() = runBlocking {
        val requestQueue = Volley.newRequestQueue(context)
        val url = "http://192.168.1.74:5000/ranking"
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    val gsonBuilder = GsonBuilder()
                    gsonBuilder.registerTypeAdapter(
                        UserRanking::class.java,
                        LeaderboardFragment.UserRankingDeserializer()
                    )
                    val users = ArrayList<UserRanking>()
                    users.addAll(
                        gsonBuilder.create().fromJson(
                            response.toString(),
                            Array<UserRanking>::class.java
                        ))
                    mranking.postValue(users)

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

    class UserRankingDeserializer : JsonDeserializer<UserRanking> {
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): UserRanking {
            val userRanking = json as JsonObject

            var username = ""
            if (userRanking.has("user")) {
                username = userRanking["user"].asString
            }

            var score = 0
            if (userRanking.has("score")) {
                score = userRanking["score"].asInt
            }

            return UserRanking(
                username,
                score,
            )
        }
    }

}