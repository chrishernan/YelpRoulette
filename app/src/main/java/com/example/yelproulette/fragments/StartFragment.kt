package com.example.yelproulette.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.yelproulette.R
import com.example.yelproulette.ViewModel.YelpViewModel


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
        val distanceSpinner : Spinner = view.findViewById(R.id.distance_spinner)
        val sortBySpinner : Spinner = view.findViewById(R.id.sort_by_spinner)

        //Creating array adapters for all 4 spinners on main screen
        createAdapter(distanceSpinner,R.array.distances_array)
        createAdapter(sortBySpinner,R.array.sort_by_spinner_array)
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

    /**
     * Creates adapter for spinners
     */
    private fun createAdapter(spinner: Spinner, array: Int) {
        ArrayAdapter.createFromResource(
            requireActivity().applicationContext,
            array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
            spinner.adapter = adapter
        }
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