package com.apm2021.rankcity.ui.leaderboard

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.apm2021.rankcity.R

class LeaderboardAdapter(val rankingList: ArrayList<UserRanking>) :
        RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder>() {

    // Describes an item view and its place within the RecyclerView
    class LeaderboardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val rankTextView: TextView = itemView.findViewById(R.id.card_leaderboard_rank)
        private val usernameTextView: TextView =
            itemView.findViewById(R.id.card_leaderboard_username)
        private val scoreTextView: TextView = itemView.findViewById(R.id.card_leaderboard_score)

        fun bind(rank: Int, username: String, score: Int) {
            rankTextView.text = rank.toString()
            usernameTextView.text = username
            scoreTextView.text = score.toString()
            changeTextColorViaRank(rank,rankTextView)
        }

        // Change text color
        private fun changeTextColorViaRank(rank: Int, rankTextView: TextView) {
            when (rank) {
                1 -> rankTextView.setTextColor(Color.parseColor("#FF03DAC5"))
                2,3 -> rankTextView.setTextColor(Color.parseColor("#FF018786"))
            }

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
        return rankingList.size
    }

    // Displays data at a certain position
    override fun onBindViewHolder(holder: LeaderboardViewHolder, position: Int) {
        holder.bind(position + 1, rankingList[position].username, rankingList[position].score)
    }

}