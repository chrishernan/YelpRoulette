package com.tenet.yelproulette.adapters

import android.content.Context
import android.widget.ArrayAdapter
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes

class CustomSpinnerAdapter(
        context: Context,
        @LayoutRes private val resource: Int,
        @IdRes private val textViewResourceId : Int) : ArrayAdapter<Any>(context,resource, textViewResourceId){

}