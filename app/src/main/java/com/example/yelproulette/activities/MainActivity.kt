package com.example.yelproulette.activities

import android.content.Intent
import android.opengl.Visibility
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.iterator
import androidx.core.view.iterator
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import androidx.lifecycle.Observer
import com.example.yelproulette.IO.YelpApiHelper
import com.example.yelproulette.R
import com.example.yelproulette.ViewModel.YelpViewModel
import com.example.yelproulette.fragments.HorizontalProgressBarFrament
import com.example.yelproulette.fragments.ProgressBarFragment
import com.example.yelproulette.fragments.SingleBusinessFragment
import com.example.yelproulette.fragments.StartFragment
import com.example.yelproulette.utils.convertMilesToMeters
import com.example.yelproulette.utils.Result
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var yelpApiHelper: YelpApiHelper
    private val viewModel : YelpViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //todo change this so it only populates once or when prompted. Not everytime activity is created
        viewModel.populateDb()
        setContentView(R.layout.activity_main)
        supportFragmentManager.commit {
            replace<StartFragment>(R.id.main_activity_fragment_container_view)
            setReorderingAllowed(true)
        }
        //todo figure out how to make observers stop observing after it's been done once.probaly resetting w/ onDestroy
        setupObservers()
    }

    /**
     * Observes viewmodel Yelp Restaurant Request and reacts accordingly
     */
    private fun setupObservers() {
        val progressBarFragment = ProgressBarFragment.newInstance()
        viewModel.randomYelpRestaurant.observe(this, Observer {
            when (it.status) {
                Result.Status.SUCCESS -> {
                    //Go to next activity
                    Timber.e("Random Business Successfully retrieved => " +
                            "Name => ${it.data!!.name} Distance => ${it.data!!.distance} " +
                            "Rating =>${it.data!!.rating}")
                    progressBarFragment.dismissAllowingStateLoss()
                    supportFragmentManager.commit {
                        replace<SingleBusinessFragment>(R.id.main_activity_fragment_container_view)
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
                    progressBarFragment.show(
                        supportFragmentManager,
                        ProgressBarFragment.TAG
                    )

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

            //todo make background for buttons and implement logic to selet and unselect other buttons.
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

        //todo Fix address value instead of hard coding irvine
        viewModel.getRandomBusiness(
            "Irvine",
            convertMilesToMeters(
                layout.findViewById<Spinner>(R.id.distance_spinner).selectedItem.toString().toInt())
                    .toString(),
            checkedRadioButton.text.toString(),
            layout.findViewById<Spinner>(R.id.open_now_spinner).selectedItem.toString(),
            layout.findViewById<Spinner>(R.id.sort_by_spinner).selectedItem.toString(),
            layout.findViewById<Spinner>(R.id.category_spinner).selectedItem.toString())

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

    fun restoreStartFragment() {
        supportFragmentManager.commit {
            replace<StartFragment>(R.id.main_activity_fragment_container_view)
            setReorderingAllowed(true)
        }
        //clears Previous random yelp restaurant and sets up observers for new action
        viewModel.clearRandomYelpRestaurant()
        setupObservers()
    }

}