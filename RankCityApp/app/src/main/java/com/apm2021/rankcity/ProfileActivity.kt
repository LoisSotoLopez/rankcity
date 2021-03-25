package com.apm2021.rankcity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

private lateinit var linearLayoutManager: LinearLayoutManager

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_profile)

        linearLayoutManager = LinearLayoutManager(this)
        //RecyclerView.layoutManager = linearLayoutManager

    }
}