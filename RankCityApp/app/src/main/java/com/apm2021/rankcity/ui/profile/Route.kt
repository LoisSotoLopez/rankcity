package com.apm2021.rankcity.ui.profile

import org.json.JSONArray

data class Route (
    var id: Int,
    var date: String,
    var title: String,
    var time: String,
    var user: String,
    var score: Int,
    var streets: ArrayList<Street>
)
