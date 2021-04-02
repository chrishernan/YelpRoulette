package com.example.yelproulette.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.iterator
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.Observer
import com.example.yelproulette.IO.YelpApiHelper
import com.example.yelproulette.R
import com.example.yelproulette.ViewModel.YelpViewModel
import com.example.yelproulette.fragments.ProgressBarFragment
import com.example.yelproulette.fragments.SingleBusinessFragment
import com.example.yelproulette.fragments.StartFragment
import com.example.yelproulette.utils.Result
import com.example.yelproulette.utils.convertMilesToMeters
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var yelpApiHelper: YelpApiHelper
    private val viewModel : YelpViewModel by viewModels()
    //location variables
    private var locationPermissionGranted = false
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var lastKnownLocation: Location? = null
    private var map: GoogleMap? = null
    private val defaultLocation = LatLng(-33.8523341, 151.2106085)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //todo change this so it only populates once or when prompted. Not everytime activity is created
        viewModel.populateDb()
        setContentView(R.layout.activity_main)
        supportFragmentManager.commit {
            replace<StartFragment>(R.id.main_activity_fragment_container_view, StartFragmentTag)
            setReorderingAllowed(true)

        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        //todo figure out how to make observers stop observing after it's been done once.probaly resetting w/ onDestroy
        setupObservers()
        getLocationPermission()
        getDeviceLocation()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        map?.let { map ->
            outState.putParcelable(KEY_CAMERA_POSITION, map.cameraPosition)
            outState.putParcelable(KEY_LOCATION, lastKnownLocation)
        }
        super.onSaveInstanceState(outState)
    }

    /**
     * Observes viewmodel Yelp Restaurant Request and reacts accordingly
     */
    @SuppressLint("BinaryOperationInTimber")
    private fun setupObservers() {
        val progressBarDialog  = ProgressBarFragment()
        viewModel.randomYelpRestaurant.observe(this, Observer {
            when (it.status) {
                Result.Status.SUCCESS -> {
                    //Go to next activity
                    Timber.e("Random Business Successfully retrieved => " +
                            "Name => ${it.data!!.name} Distance => ${it.data.distance} " +
                            "Rating =>${it.data.rating}")
                    progressBarDialog.dismissAllowingStateLoss()

                    supportFragmentManager.commit {
                        replace<SingleBusinessFragment>(
                                R.id.main_activity_fragment_container_view,
                                SingleBusinessFragmentTag)
                        setReorderingAllowed(true)
                    }
                    stopObservingRandomYelpBusiness()
                }
                Result.Status.ZERO -> {
                    //todo display dialog saying we couldn't find any restaurants matching the current stuff
                    Timber.e("Zero businesses returned")
                }
                Result.Status.LOADING -> {
                    //display a Progress Bar.
                    progressBarDialog.show(supportFragmentManager,ProgressBarFragment.TAG)

                }
                Result.Status.ERROR -> {
                    //todo display dialog saying there was an error
                }


            }
        })
    }

    private fun stopObservingRandomYelpBusiness() {
        viewModel.randomYelpRestaurant.removeObservers(this)
    }

    /**
     * Changes background to show radio button is selected.
     */
    fun onPriceButtonClick(view: View) {
        val radioGroup = view.parent as RadioGroup
        if(view is RadioButton) {
            val checked = view.isChecked

            //todo make background for buttons and implement logic to select and unselect other buttons.
            when(view.id) {
                R.id.one_dollar_sign_radio_button ->
                    if(checked && !checkIfRadioButtonIsAlreadyChecked(view)) {
                        view.background = ContextCompat.getDrawable(
                                applicationContext,
                                R.drawable.price_selected_background)
                        uncheckPreviousSelection(radioGroup,view.id)

                    }
                R.id.two_dollar_sign_radio_button ->
                    if(checked && !checkIfRadioButtonIsAlreadyChecked(view)) {
                        view.background = ContextCompat.getDrawable(
                                applicationContext,
                                R.drawable.price_selected_background)
                        uncheckPreviousSelection(radioGroup,view.id)
                    }
                R.id.three_dollar_sign_radio_button ->
                    if(checked && !checkIfRadioButtonIsAlreadyChecked(view)) {
                        view.background =  ContextCompat.getDrawable(
                                applicationContext,
                                R.drawable.price_selected_background)
                        uncheckPreviousSelection(radioGroup,view.id)
                    }
                R.id.four_dollar_sign_radio_button ->
                    if(checked && !checkIfRadioButtonIsAlreadyChecked(view)) {
                        view.background =  ContextCompat.getDrawable(
                                applicationContext,
                                R.drawable.price_selected_background)
                        uncheckPreviousSelection(radioGroup,view.id)
                    }
            }
        }
    }

    /**
     * Will start repository requests and send user to loading activity
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun onSpinButtonClick(view : View) {
        val layout = view.parent as ConstraintLayout
        Timber.e("Distance => ${layout.findViewById<Spinner>(R.id.distance_spinner).selectedItem}")
        Timber.e("Category => ${layout.findViewById<Spinner>(R.id.category_spinner).selectedItem}")
        Timber.e("sort by => ${layout.findViewById<Spinner>(R.id.sort_by_spinner).selectedItem}")
        Timber.e("open now => ${layout.findViewById<Spinner>(R.id.open_now_spinner).selectedItem}")
        val checkedRadioButton : RadioButton = findViewById(layout
            .findViewById<RadioGroup>(R.id.price_radio_group).checkedRadioButtonId)
        Timber.e("price => ${checkedRadioButton.text}")


        if(!locationPermissionGranted) {
            Toast.makeText(
                    this,
                    "YelpRoulette Needs location permission to suggest restaurants",
                    Toast.LENGTH_SHORT).show()
            getLocationPermission()
            getDeviceLocation()
        }
        else {
            Timber.e("Current Longitude => ${lastKnownLocation?.longitude.toString()}")
            Timber.e("Current latitude => ${lastKnownLocation?.latitude.toString()}")

            viewModel.getRandomBusinessWithLongLat(
                    lastKnownLocation?.longitude.toString(),
                    lastKnownLocation?.latitude.toString(),
                    layout.findViewById<Spinner>(R.id.distance_spinner).selectedItem.toString(),
                    checkedRadioButton.text.toString(),
                    layout.findViewById<Spinner>(R.id.open_now_spinner).selectedItem.toString(),
                    layout.findViewById<Spinner>(R.id.sort_by_spinner).selectedItem.toString(),
                    layout.findViewById<Spinner>(R.id.category_spinner).selectedItem.toString())
        }


    }

    /**
     * Unchecks/unselects previous price selection
     */
    private fun uncheckPreviousSelection(radioGroup : RadioGroup, checkedRadioButtonId: Int) {
        for(radioButton in radioGroup) {
            if(radioButton.id != checkedRadioButtonId) {
                findViewById<RadioButton>(radioButton.id).background = ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.price_unselected_background)

            }
        }
    }

    private fun checkIfRadioButtonIsAlreadyChecked(radioButton: RadioButton) : Boolean {
        //drawable is equal to our R.drawable.price_selected_background
        return radioButton.background == ContextCompat.getDrawable(
                applicationContext,
                R.drawable.price_selected_background)
    }

    fun goToDialer(phoneNumber : String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$phoneNumber")
        startActivity(intent)
    }

    /**
     * Sends user to google maps with business location.
     */
    fun goToGoogleMapsApp(address : String) {
        // Create a Uri from an intent string. Use the result to create an Intent.
        val gmmIntentUri = Uri
                .parse("geo:0,0?q=$address")

        // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        // Make the Intent explicit by setting the Google Maps package
        mapIntent.setPackage("com.google.android.apps.maps")

        // Attempt to start an activity that can handle the Intent
        startActivity(mapIntent)

    }

    /**
     * Sends user to the Yelp website (or app)
     */
    fun goToYelpBusiness(yelpWebsite: String) {
        val yelpUri = Uri.parse(yelpWebsite)
        val intent = Intent(Intent.ACTION_VIEW,yelpUri)
        startActivity(intent)
    }

    fun restoreStartFragment() {
        supportFragmentManager.commit {
            replace<StartFragment>(R.id.main_activity_fragment_container_view)
            setReorderingAllowed(true)
        }
        //clears Previous random yelp restaurant and sets up observers for new action
        viewModel.clearRandomYelpRestaurant()
        setupObservers()
    }


    fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.applicationContext,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                }
            }
        }
    }

    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result

                    } else {
                        Timber.e("Current location is null. Using defaults.")
                        Timber.e( "Exception: ${task.exception}")
//                        Same thing. Not needed. We just need the latitude, longitude
//                        map?.moveCamera(CameraUpdateFactory
//                                .newLatLngZoom(defaultLocation, DEFAULT_ZOOM.toFloat()))
//                        map?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Timber.e("Security Exception")
            Timber.e(e)
        }
    }

    companion object {
        const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
        private const val DEFAULT_ZOOM = 15

        // Keys for storing activity state.
        // [START maps_current_place_state_keys]
        private const val KEY_CAMERA_POSITION = "camera_position"
        private const val KEY_LOCATION = "location"
        // [END maps_current_place_state_keys]

        //Fragment tags
        private const val StartFragmentTag = "StartFragment"
        private const val SingleBusinessFragmentTag = "SingleBusinessFragment"

    }

}