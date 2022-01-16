package com.tenet.yelproulette.ViewModel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.tenet.yelproulette.IO.Repository
import com.tenet.yelproulette.retrofitDataModels.BusinessesItem
import com.tenet.yelproulette.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class YelpViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel(){


    private val viewModelCoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    var randomYelpRestaurant = MutableLiveData<Result<BusinessesItem?>>()
    private val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, exception ->
        Timber.e("Exception Thrown => $exception")
        randomYelpRestaurant.postValue(Result(
                Result.Status.ERROR,
                null,
                "Exception Thrown => ${exception.message.toString()} \n " +
                        "Exception Cause ${exception.cause.toString()}"
        ))
    }

    /**
     * Asks repository to fetch businesses  with current address.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun getRandomBusiness(address : String,
                          radius : String,
                          price : String,
                          openNow : String,
                          sortBy : String,
                          term : String) {
        viewModelCoroutineScope.launch(coroutineExceptionHandler) {
            //Letting Our view know we are loading the random business
            randomYelpRestaurant.postValue(Result(
                    Result.Status.LOADING,
                    null,
                    "Loading Random Business"
            ))
            val businessJob = viewModelCoroutineScope.async {
                    repository.fetchTermBusiness(
                        address,
                        radius,
                        price,
                        openNow,
                        sortBy,
                        term)
            }
            val business = businessJob.await()
            //Posting Success or Zero Business value depending on the coroutine result
            populateResultLiveData(business)
        }
    }

    /**
     * Asks repository to fetch businesses when we have longitude and latitude.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun getRandomBusinessWithLongLat(longitude : String,
                                     latitude : String,
                                     radius : String,
                                     price : String,
                                     openNow : String,
                                     sortBy : String,
                                     term : String) {
        viewModelCoroutineScope.launch(coroutineExceptionHandler) {
            randomYelpRestaurant.postValue(Result(
                    Result.Status.LOADING,
                    null,
                    "Loading Random Business"
            ))
            val latitudeLongitudeJob = viewModelCoroutineScope.async {
                repository.fetchTermLatitudeLongitudeBusiness(
                    longitude,
                    latitude,
                    radius,
                    price,
                    openNow,
                    sortBy,
                    term)
            }
            val business = latitudeLongitudeJob.await()
            populateResultLiveData(business)
        }
    }

    /**
     * Post Result Value to YelpRestaurant Livedata depending on what is returned
     * from Repository
     */
    private fun populateResultLiveData(returnedBusiness : BusinessesItem?) {
        if(returnedBusiness == null) {
            randomYelpRestaurant.postValue(Result(
                    Result.Status.ZERO,
                    null,
                    "No Results Retrieved with with current selections"
            ))
        }
        else {
            randomYelpRestaurant.postValue(Result(
                    Result.Status.SUCCESS,
                    returnedBusiness,
                    "Successfully retrieved Random Yelp restaurant"
            ))
        }
    }

    /**
     * Populates Room Db upon creation of activity with mappings used in API requests
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun populateDb() {
        viewModelCoroutineScope.launch(coroutineExceptionHandler) {
            val populateDbJob = viewModelCoroutineScope.async {
                repository.addAllMappingsToDB()
            }
            populateDbJob.await()
        }

    }

    fun clearRandomYelpRestaurant() {
        randomYelpRestaurant = MutableLiveData<Result<BusinessesItem?>>()
    }

}