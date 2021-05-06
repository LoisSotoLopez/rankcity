package com.apm2021.rankcity.ui.leaderboard

import android.content.Context
import com.apm2021.rankcity.R

class Datasource_Leaderboard(val context: LeaderboardFragment) {
    fun getUsersList(): Array<String> {

        // Return users list from string resources
        return context.resources.getStringArray(R.array.users_array)
    }
}