package com.apm2021.rankcity.ui.leaderboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.apm2021.rankcity.R

class LeaderboardFragment : Fragment() {

    private lateinit var leaderboardViewModel: LeaderboardViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        leaderboardViewModel =
                ViewModelProvider(this).get(LeaderboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_leaderboard, container, false)
        val textView: TextView = root.findViewById(R.id.text_dashboard)
        leaderboardViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}