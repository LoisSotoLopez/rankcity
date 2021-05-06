package com.apm2021.rankcity.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.apm2021.rankcity.MapsActivity
import com.apm2021.rankcity.R


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        // Boton a empezar
        val startRouteButton = root.findViewById(R.id.period_change) as Button;
        startRouteButton.setOnClickListener {
            switchActivities();
        }
        return root;
    }

    private fun switchActivities() {
        val intent = Intent(activity, MapsActivity::class.java)
        startActivity(intent)
    }
}