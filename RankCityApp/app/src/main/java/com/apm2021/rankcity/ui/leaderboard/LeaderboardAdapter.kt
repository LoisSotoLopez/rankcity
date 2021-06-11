package com.apm2021.rankcity.ui.leaderboard

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.apm2021.rankcity.R

class LeaderboardAdapter(val routesList: List<String>) :
        RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder>() {

    // Describes an item view and its place within the RecyclerView
    class LeaderboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val flowerTextView: TextView = itemView.findViewById(R.id.card_user_name)

        fun bind(word: String) {
            flowerTextView.text = word
        }
    }

    // Returns a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_leaderboard, parent, false)

        return LeaderboardViewHolder(view)
    }

    // Returns size of data list
    override fun getItemCount(): Int {
        return routesList.size
    }

    // Displays data at a certain position
    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        holder.bind(routesList[position] as String)
    }
}