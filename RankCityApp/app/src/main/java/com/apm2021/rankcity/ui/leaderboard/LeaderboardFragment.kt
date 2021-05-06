package com.apm2021.rankcity.ui.leaderboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apm2021.rankcity.R

class LeaderboardFragment : Fragment() {

    private lateinit var leaderboardViewModel: LeaderboardViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_leaderboard, container, false)
    }

    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)

        val usersList = Datasource_Leaderboard(this).getUsersList()


        val recyclerView = itemView.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.apply {
            // set a LinearLayoutManager to handle Android
            // RecyclerView behavior
            layoutManager = LinearLayoutManager(activity)
            // set the custom adapter to the RecyclerView
            adapter = LeaderboardAdapter(usersList)
        }
    }
}