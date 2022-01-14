package com.example.yelproulette.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.util.Log.e
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.iterator
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.Observer
import com.example.yelproulette.IO.YelpApiHelper
import com.example.yelproulette.R
import com.example.yelproulette.ViewModel.YelpViewModel
import com.example.yelproulette.fragments.*
import com.example.yelproulette.utils.Result
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint
import org.angmarch.views.NiceSpinner
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
    private var isLocationRequestDone = false
    private var map: GoogleMap? = null
    private val defaultLocation = LatLng(-33.8523341, 151.2106085)
    val fetchingLocationDialog = FetchingLocationDialog()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        viewModel.populateDb()
        setContentView(R.layout.activity_main)
        supportFragmentManager.commit {
            replace<StartFragment>(R.id.main_activity_fragment_container_view, StartFragmentTag)
            setReorderingAllowed(true)

        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fetchingLocationDialog.setCancelable(false)
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
        val zeroBusinessDialog = ZeroBusinessesDialogFragment()
        val errorDialog = ErrorDialogFragment()
        viewModel.randomYelpRestaurant.observe(this, Observer {
            when (it.status) {
                Result.Status.SUCCESS -> {
                    //Go to next activity
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
                    progressBarDialog.dismissAllowingStateLoss()
                    zeroBusinessDialog.show(supportFragmentManager,ZeroBusinessesDialogFragment.TAG)
                }
                Result.Status.LOADING -> {
                    //display a Progress Bar.
                    progressBarDialog.show(supportFragmentManager,ProgressBarFragment.TAG)

                }
                Result.Status.ERROR -> {
                    errorDialog.show(supportFragmentManager,ErrorDialogFragment.TAG)
                    //errorDialog.requireView().findViewById<TextView>(R.id.text_view_error_dialog_fragment)
                        //.text = it.message.toString()
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

        if(view is android.widget.Button) {
            when(view.id) {
                R.id.one_dollar_sign_button ->
                    if(view.isPressed and !view.isSelected) {
                        view.isSelected = true
                    }
                    else if (view.isPressed and view.isSelected) {
                        view.isSelected = false
                    }
                R.id.two_dollar_sign_button ->
                    if(view.isPressed and !view.isSelected) {
                        view.isSelected = true
                    }
                    else if (view.isPressed and view.isSelected) {
                        view.isSelected = false
                    }
                R.id.three_dollar_sign_button ->
                    if(view.isPressed and !view.isSelected) {
                        view.isSelected = true
                    }
                    else if (view.isPressed and view.isSelected) {
                        view.isSelected = false
                    }
                R.id.four_dollar_sign_button ->
                    if(view.isPressed and !view.isSelected) {
                        view.isSelected = true
                    }
                    else if (view.isPressed and view.isSelected) {
                        view.isSelected = false
                    }
            }
        }
    }

    fun onOpenNowButtonClick(view : View) {
        val radioGroup = view.parent as RadioGroup
        if(view is RadioButton) {
            val checked = view.isChecked

            when(view.id) {
                R.id.open_now_true ->
                    if(checked && !checkIfRadioButtonIsAlreadyChecked(view)) {
                        view.background = ContextCompat.getDrawable(
                            applicationContext,
                            R.drawable.open_now_selected_background)
                        uncheckPreviousSelection(radioGroup,view.id)

                    }
                R.id.open_now_false ->
                    if(checked && !checkIfRadioButtonIsAlreadyChecked(view)) {
                        view.background = ContextCompat.getDrawable(
                            applicationContext,
                            R.drawable.open_now_selected_background)
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
        val locationNullFragment  = LocationNullFragment()
        val layout = view.parent as ConstraintLayout
        val priceSelectedButtons = generateYelpPriceArgument()
            //findViewById(layout.findViewById<RadioGroup>(R.id.price_radio_group).checkedRadioButtonId)
        val openNowCheckedRadioButton : RadioButton = findViewById(layout
            .findViewById<RadioGroup>(R.id.open_now_radio_group).checkedRadioButtonId)


        if(!locationPermissionGranted) {
            Toast.makeText(
                    this,
                    "YelpRoulette Needs location permission to suggest restaurants",
                    Toast.LENGTH_SHORT).show()
            getLocationPermission()
            getDeviceLocation()
        }
        else {
            if(lastKnownLocation == null && isLocationRequestDone) {
                locationNullFragment.show(supportFragmentManager,LocationNullFragment.TAG)
            }
            else if(isLocationRequestDone && lastKnownLocation!=null) {

                viewModel.getRandomBusinessWithLongLat(
                    lastKnownLocation?.longitude.toString(),
                    lastKnownLocation?.latitude.toString(),
                    layout.findViewById<NiceSpinner>(R.id.distance_spinner).selectedItem.toString(),
                    priceSelectedButtons.toString(),
                    openNowCheckedRadioButton.text.toString(),
                    layout.findViewById<NiceSpinner>(R.id.sort_by_spinner).selectedItem.toString(),
                    layout.findViewById<TextInputEditText>(R.id.term_text_input_edit_text)
                        .text.toString().lowercase()
                )
            }
            else {
                getDeviceLocation()
            }
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
                        R.drawable.open_now_unselected_background)
            }
        }
    }

    private fun checkIfRadioButtonIsAlreadyChecked(radioButton: RadioButton) : Boolean {
        //drawable is equal to our R.drawable.price_selected_background
        return radioButton.background == ContextCompat.getDrawable(
                applicationContext,
                R.drawable.open_now_selected_background)
    }

    /**
     *  Generates string used by yelp api to retrieve business within a certain price range
     */
    private fun generateYelpPriceArgument() : String {
        val priceButtonLinearLayout : LinearLayout = findViewById(R.id.price_button_linear_layout)
        var priceString = ""
        var priceList = mutableListOf<String>()
        for(button in priceButtonLinearLayout) {
            if(button.isSelected) {
                priceList.add((button as android.widget.Button).text.length.toString())
            }
        }
        //returns all prices if no selections have been made for price points.
        if(priceList.isEmpty()) return "1,2,3,4"

        priceString = priceList.joinToString(separator = ",")
        return priceString
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


    @RequiresApi(Build.VERSION_CODES.M)
    fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */

        when {
            ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You can use the API that requires the permission.
                locationPermissionGranted = true
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
            // In an educational UI, explain to the user why your app requires this
            // permission for a specific feature to behave as expected. In this UI,
            // include a "cancel" or "no thanks" button that allows the user to
            // continue using your app without granting the permission.

        }
            else -> {
                // You can directly ask for the permission.
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
            }
        }


            /* old method
       if (ContextCompat.checkSelfPermission(this.applicationContext,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }*/
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val locationPermissionDeniedFragment  = LocationPermissionDeniedFragment()
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                    getDeviceLocation()
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    locationPermissionDeniedFragment.show(supportFragmentManager,LocationPermissionDeniedFragment.TAG)
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
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
                fetchOrDismissFetchingLocationDialog(isLocationRequestDone)
                    fusedLocationProviderClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY,null)
                        .addOnSuccessListener { location: Location? ->
                            // Set the map's camera position to the current location of the device.
                            isLocationRequestDone = true
                            fetchOrDismissFetchingLocationDialog(isLocationRequestDone)
                            lastKnownLocation = location
                        }
                        .addOnCanceledListener {
                            Timber.e("Location is cancelled")
                            isLocationRequestDone = false
                        }
                        .addOnFailureListener {
                            Timber.e("location failed")
                            isLocationRequestDone = false
                        }
            }
        } catch (e: SecurityException) {
            Timber.e(e)
        }
    }

    private fun fetchOrDismissFetchingLocationDialog(isLocationRequestDone : Boolean) {
        if(isLocationRequestDone){
            fetchingLocationDialog.setCancelable(true)
            fetchingLocationDialog.dismiss()
        }else {
            fetchingLocationDialog.show(supportFragmentManager,FetchingLocationDialog.TAG)
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