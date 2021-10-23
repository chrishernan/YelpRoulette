package com.example.yelproulette.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.yelproulette.R
import com.example.yelproulette.ViewModel.YelpViewModel
import org.angmarch.views.NiceSpinner
import timber.log.Timber
import java.util.*


/**
 * A simple [Fragment] subclass.
 * Use the [StartFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StartFragment : Fragment() {
    private val viewModel : YelpViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_start, container, false)
        val distanceSpinner : NiceSpinner = view.findViewById(R.id.distance_spinner)
        val sortBySpinner : NiceSpinner = view.findViewById(R.id.sort_by_spinner)
        val onedollarSignButton : android.widget.Button = view.findViewById(R.id.one_dollar_sign_button)
        onedollarSignButton.isSelected = true

        val distanceList : List<String> = resources.getStringArray(R.array.distances_array).toList()
        val sortByList : List<String> = resources.getStringArray(R.array.sort_by_spinner_array).toList()

        distanceSpinner.attachDataSource(distanceList)
        sortBySpinner.attachDataSource(sortByList)

        // Inflate the layout for this fragment
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment StartFragment.
         */
        @JvmStatic
        fun newInstance() =
            StartFragment()
    }



    fun displayProgressBarDialog() {

        ProgressBarFragment().show(
                childFragmentManager,
                ProgressBarFragment.TAG
        )
    }

    fun dismissProgressBarDialog() {
        ProgressBarFragment().dismissAllowingStateLoss()
    }

}