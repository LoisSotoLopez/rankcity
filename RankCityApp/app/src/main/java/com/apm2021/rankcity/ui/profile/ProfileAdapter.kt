package com.apm2021.rankcity.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.apm2021.rankcity.R

class ProfileAdapter(val routesList: ArrayList<Route>) :
        RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>() {

    // Describes an item view and its place within the RecyclerView
    class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.card_user_date)
        private val scoreTextView: TextView = itemView.findViewById(R.id.card_user_score)

        fun bind(title: String, score: Int) {
            titleTextView.text = title
            scoreTextView.text = score.toString()
        }
    }

    // Returns a new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_profile, parent, false)

        return ProfileViewHolder(view)
    }

    // Returns size of data list
    override fun getItemCount(): Int {
        return routesList.size
    }

    // Displays data at a certain position
    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        holder.bind(routesList[position].title, routesList[position].score)
    }
}