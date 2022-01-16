package com.tenet.yelproulette.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.tenet.yelproulette.R

class ProgressBarFragment : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        //inflater.inflate(R.layout.fragment_progress_bar_fragment,container,false)
        return inflater.inflate(R.layout.fragment_progress_bar_fragment,container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment ErrorFragment.
         */

        const val TAG = "ProgressBarFragment"

        @JvmStatic
        fun newInstance() =
                ProgressBarFragment()
    }

}