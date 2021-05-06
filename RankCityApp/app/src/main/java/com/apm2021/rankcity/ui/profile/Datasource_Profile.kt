package com.apm2021.rankcity.ui.profile

import com.apm2021.rankcity.R

class Datasource_Profile(val context: ProfileFragment) {
    fun getDatesList(): Array<String> {

        // Return users list from string resources
        return context.resources.getStringArray(R.array.dates_array)
    }
}