package com.example.yelproulette.utils

import timber.log.Timber

fun convertMilesToMeters(miles: Int) : Int {
    val meters = miles*Constants.METER_TO_MILE
    Timber.e("Miles => $miles  :   Meters => $meters")
    return meters
}


