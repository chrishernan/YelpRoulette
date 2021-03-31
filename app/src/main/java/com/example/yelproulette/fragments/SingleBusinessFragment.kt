package com.example.yelproulette.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.yelproulette.R
import com.example.yelproulette.ViewModel.YelpViewModel
import com.example.yelproulette.activities.MainActivity
import com.example.yelproulette.utils.Constants
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import timber.log.Timber


/**
 * A simple [Fragment] subclass.
 * Use the [SingleBusinessFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SingleBusinessFragment : Fragment(){
    private val viewModel : YelpViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                (activity as MainActivity).restoreStartFragment()
            }

        })

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_single_business, container, false)
        val yelpRestaurant =  viewModel.randomYelpRestaurant.value!!.data!!
        view.findViewById<TextView>(R.id.number_of_ratings_text_view).text = yelpRestaurant.reviewCount.toString()
        //Inserts image from url to business image view
        Glide.with(this).load(yelpRestaurant.imageUrl)
            .centerCrop()
            .into(view.findViewById(R.id.business_image_view))
        view.findViewById<TextView>(R.id.business_title_text_view).text = yelpRestaurant.name
        view.findViewById<RatingBar>(R.id.business_rating_bar).rating = yelpRestaurant.rating!!.toFloat()

        //setting up google map and rendering it
        val mapView = view.findViewById<MapView>(R.id.map)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        try{
            MapsInitializer.initialize(activity?.applicationContext)
        } catch( e : Exception) {
            e.printStackTrace()
        }

        mapView.getMapAsync( OnMapReadyCallback {
            it.apply {
                Timber.e("Latitude => ${viewModel.randomYelpRestaurant.value!!.data!!.coordinates!!.latitude!!}")
                Timber.e("Longitude => ${viewModel.randomYelpRestaurant.value!!.data!!.coordinates!!.longitude!!}")
                val currentRestaurant = LatLng(
                        viewModel.randomYelpRestaurant.value!!.data!!.coordinates!!.latitude!!,
                        viewModel.randomYelpRestaurant.value!!.data!!.coordinates!!.longitude!!)
                addMarker(
                        MarkerOptions()
                                .position(currentRestaurant)
                                .title("Current Restaurant")
                )
                moveCamera(CameraUpdateFactory.newLatLngZoom(currentRestaurant, Constants.STREET_ZOOM_LEVEL))
            }
        })

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment SingleBusinessFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = SingleBusinessFragment()
    }
}