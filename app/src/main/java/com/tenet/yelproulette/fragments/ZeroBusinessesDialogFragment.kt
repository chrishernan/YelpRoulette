package com.tenet.yelproulette.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.tenet.yelproulette.R



/**
 * A simple [Fragment] subclass.
 * Use the [ZeroBusinessesDialogFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ZeroBusinessesDialogFragment : DialogFragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_zero_businesses_dialog, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         * @return A new instance of fragment ZeroBusinessesDialogFragment.
         */
        @JvmStatic
        fun newInstance() = ZeroBusinessesDialogFragment()

        const val TAG = "ZeroBusinessFragment"
    }
}