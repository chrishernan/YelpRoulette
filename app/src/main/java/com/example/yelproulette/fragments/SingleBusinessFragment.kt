package com.example.yelproulette.fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
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
class SingleBusinessFragment : Fragment() {
    private val viewModel : YelpViewModel by activityViewModels()
    //Setting up google map and rendering it
    private lateinit var mapView : MapView

    private val MAPVIEW_BUNDLE_KEY : String = "MapViewBundleKey"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                (activity as MainActivity).restoreStartFragment()
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_single_business, container, false)

        setOnClickListenersForImageButtons(view)
        val yelpRestaurant =  viewModel.randomYelpRestaurant.value!!.data!!
        view.findViewById<TextView>(R.id.number_of_ratings_text_view).text = yelpRestaurant.reviewCount.toString()
        //Inserts image from url to business image view
        Glide.with(this).load(yelpRestaurant.imageUrl)
            .centerCrop()
            .into(view.findViewById(R.id.business_image_view))
        view.findViewById<TextView>(R.id.business_title_text_view).text = yelpRestaurant.name
        view.findViewById<RatingBar>(R.id.business_rating_bar).rating = yelpRestaurant.rating!!.toFloat()
        view.findViewById<TextView>(R.id.single_business_price_text_view).text = yelpRestaurant.price!!

        //Map View Set up
        var mapViewBundle : Bundle? = null
        if(savedInstanceState != null){
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY)
        }
        mapView = view.findViewById(R.id.map)
        mapView.onCreate(mapViewBundle)
        mapView.onResume()
        try{
            MapsInitializer.initialize(activity?.applicationContext)
        } catch( e : Exception) {
            e.printStackTrace()
        }

        mapView.getMapAsync( OnMapReadyCallback {
            it.apply {
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY)
        if (mapViewBundle == null) {
            mapViewBundle = Bundle()
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle)
        }
        mapView.onSaveInstanceState(mapViewBundle)
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }


    /**
     * Will call subsequent functions to set listeners for ImageButtons in this page
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun setOnClickListenersForImageButtons(view: View) {
        setonClickListenerForCallButton(view)
        setonClickListenerForDirectionsButton(view)
        setonClickListenerForYelpButton(view)
    }

    private fun setonClickListenerForCallButton(view: View) {
        val callImageButton = view.findViewById<ImageButton>(R.id.business_call_button)
        callImageButton.setOnClickListener {
            val displayPhoneNumber = viewModel.randomYelpRestaurant.value!!.data!!.displayPhone
            val phoneNumber = viewModel.randomYelpRestaurant.value!!.data!!.phone
            if (phoneNumber != null || displayPhoneNumber != null){
                if(phoneNumber == null) {
                    (activity as MainActivity).goToDialer(displayPhoneNumber!!)
                }
                else {
                    (activity as MainActivity).goToDialer(phoneNumber)
                }
            } else {
                Toast.makeText(
                        activity?.applicationContext,
                        "This Business does not have a phone number registered with Yelp",
                        Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setonClickListenerForDirectionsButton(view: View) {
        val directionsButton = view.findViewById<ImageButton>(R.id.business_directions_button)
        directionsButton.setOnClickListener {
            val fullAddress = viewModel.randomYelpRestaurant.value!!.data!!.location
            if(fullAddress?.address1?.isNotEmpty() == true) {

                val address = fullAddress.displayAddress?.joinToString()
                (activity as MainActivity).getLocationPermission()
                if (address != null) {
                    (activity as MainActivity).goToGoogleMapsApp(address)
                }


            } else {
                Toast.makeText(
                        activity?.applicationContext,
                        "This Business does not have an address registered with Yelp",
                        Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun setonClickListenerForYelpButton(view: View) {
        val yelpImageButton = view.findViewById<ImageButton>(R.id.business_yelp_button)
        yelpImageButton.setOnClickListener {
            val yelpWebsite = viewModel.randomYelpRestaurant.value!!.data!!.url
            if (yelpWebsite != null) {
                (activity as MainActivity).goToYelpBusiness(yelpWebsite)
            } else {
                Toast.makeText(
                        activity?.applicationContext,
                        "This Business does not have a website registered with Yelp",
                        Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Small functions to format address fields and avoid nested ifs
     */
    private fun formatAddress1(address1 : String) : String{
        return address1.replace(" ", "+")
    }

    private fun formatAddress2(address2 : String) : String {
        return if(address2.isNotEmpty()) {
            address2.replace(" ","+")
        }
        else {
            ""
        }
    }

    private fun formatAddress3(address3 : String) : String {
        return if(address3.isNotEmpty()) {
            address3.replace(" ","+")
        }
        else {
            ""
        }
    }

    private fun formatCity(city : String) : String {
        return if(city.isNotEmpty()) {
            "+"+city.replace(" ","+")
        }
        else {
            "+"
        }
    }

    private fun formatState(state : String) : String {
        return if(state.isNotEmpty()) {
            "+"+state.replace(" ","+")
        }
        else {
            "+"
        }
    }

    private fun formatZipCode(zipCode : String) : String {
        return "+"+zipCode
    }

    private fun formatCountry(country : String) : String
    {
        return "+"+country
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment SingleBusinessFragment.
         */
        @JvmStatic
        fun newInstance() = SingleBusinessFragment()

    }
}