package com.example.yelproulette.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.yelproulette.R

/**
 * A simple [Fragment] subclass.
 * Use the [LocationPermissionDeniedFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LocationPermissionDeniedFragment : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location_permission_denied, container, false)
    }

    companion object {
        const val TAG = "LocationPermissionDeniedFragment"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment LocationPermissionDeniedFragment.
         */
        @JvmStatic
        fun newInstance(param1: String, param2: String) = LocationPermissionDeniedFragment()
    }
}