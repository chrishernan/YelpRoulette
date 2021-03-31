package com.example.yelproulette.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.example.yelproulette.R
import com.example.yelproulette.ViewModel.YelpViewModel
import com.example.yelproulette.activities.MainActivity


/**
 * A simple [Fragment] subclass.
 * Use the [StartFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StartFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private val viewModel : YelpViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_start, container, false)
        val distanceSpinner : Spinner = view.findViewById(R.id.distance_spinner)
        val categorySpinner : Spinner = view.findViewById(R.id.category_spinner)
        val sortBySpinner : Spinner = view.findViewById(R.id.sort_by_spinner)
        val openNowSpinner : Spinner = view.findViewById(R.id.open_now_spinner)

        //Creating array adapters for all 4 spinners on main screen
        createAdapter(distanceSpinner,R.array.distances_array)
        createAdapter(categorySpinner,R.array.categories_array)
        createAdapter(sortBySpinner,R.array.sort_by_spinner_array)
        createAdapter(openNowSpinner,R.array.open_now_spinner_array)
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
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
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
}