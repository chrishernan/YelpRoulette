package com.example.yelproulette.IO

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.yelproulette.DependencyInjection.AppModule
import com.example.yelproulette.retrofitDataModels.Businesses
import com.example.yelproulette.retrofitDataModels.BusinessesItem
import com.example.yelproulette.room.ApiMapDatabase
import com.example.yelproulette.room.SortByMapDao
import com.example.yelproulette.utils.Constants
import com.example.yelproulette.utils.convertMilesToMeters
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.O)
class Repository @Inject constructor(
    private val yelpApiHelper: YelpApiHelper,
    private val apiMapDatabase : ApiMapDatabase,
    private val sortByMapDao: SortByMapDao,
    @AppModule.SortByMapStringArray private val sortByMapStringArray: Array<String>) {

    //Initializing coroutine variables
    private var isDatabasePopulating = false
    val cpuScope = CoroutineScope(Dispatchers.Default)
    private val ioCoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, exception ->
        Timber.e("Exception Thrown => $exception")
    }

    suspend fun fetchTermBusiness(address : String,
                                      radius : String,
                                      price : String,
                                      openNow : String,
                                      sortBy : String,
                                      term : String) : BusinessesItem? {
        //If categories is set to Any call different function to handle that
        if(term.isEmpty()) {
            return fetchNoTermBusiness(
                address,
                radius,
                price,
                openNow,
                sortBy)
        }
        //else, if there is a category, handle in this function
        else {
            val businesses = yelpApiHelper.getBusinesses(
                    address,
                    convertMilesToMeters(radius.toInt()).toString(),
                    price.toString(),
                    openNowConversion(openNow),
                    sortByMapDao.getApiSortByKey(sortBy),
                    term)

            //Computes random business from return businesses
            val randomBusinessJob  = cpuScope.async { randomBusiness(businesses)}
            val randomBus = randomBusinessJob.await()
            return randomBus
        }

    }

    /**
     * Fetches businesses when category is set to "Any"
     */
    suspend fun fetchNoTermBusiness(address : String,
                                        radius : String,
                                        price : String,
                                        openNow : String,
                                        sortBy : String) : BusinessesItem? {
        val businesses = yelpApiHelper.getBusinessesNoTerm(
            address,
            convertMilesToMeters(radius.toInt()).toString(),
            price.toString(),
            openNowConversion(openNow),
            sortByMapDao.getApiSortByKey(sortBy))

        //Computes random business from return businesses
        val randomBusinessJob  = cpuScope.async { randomBusiness(businesses)}
        val randomBus = randomBusinessJob.await()
        return randomBus
    }

    /**
     * Fetches term business with Latitude and Longitude
     */
    suspend fun fetchTermLatitudeLongitudeBusiness(longitude : String,
                                                       latitude : String,
                                                       radius : String,
                                                       price : String,
                                                       openNow : String,
                                                       sortBy : String,
                                                       term : String): BusinessesItem? {
        if(term.isEmpty()) {
            return fetchNoTermLatitudeLongitudeBusiness(
                longitude,
                latitude,
                radius,
                price,
                openNow,
                sortBy)
        } else {
            val businesses = yelpApiHelper.getBusinessesWithLongitudeLatitude(
                    longitude,
                    latitude,
                    convertMilesToMeters(radius.toInt()).toString(),
                    price.toString(),
                    openNowConversion(openNow),
                    sortByMapDao.getApiSortByKey(sortBy),
                    term)

            //Computes random business from return businesses
            val randomBusinessJob  = cpuScope.async { randomBusiness(businesses)}
            val randomBus = randomBusinessJob.await()
            return randomBus
        }

    }

    /**
     * Fetches any business with Latitude and Longitude when category is set to "Any"
     */
    suspend fun fetchNoTermLatitudeLongitudeBusiness(longitude : String,
                                                         latitude : String,
                                                         radius : String,
                                                         price : String,
                                                         openNow : String,
                                                         sortBy : String) : BusinessesItem? {
        val businesses = yelpApiHelper.getBusinessesWithLongitudeLatitudeNoTerm(
            longitude,
            latitude,
            convertMilesToMeters(radius.toInt()).toString(),
            price.toString(),
            openNowConversion(openNow),
            sortByMapDao.getApiSortByKey(sortBy)
        )

        //Computes random business from return businesses
        val randomBusinessJob  = cpuScope.async { randomBusiness(businesses)}
        val randomBus = randomBusinessJob.await()
        return randomBus
    }



    /**
     * Returns a random business from YelpApiResponse
     */
    private fun randomBusiness(businesses : Businesses) : BusinessesItem? {
        return if(businesses.total != 0) {
            val ran = businesses.businesses?.let { Random.nextInt(0, it.size) }
            val randomBusiness = ran?.let { businesses.businesses[it] }
            randomBusiness
        } else{
            null
        }
    }

    /**
     * Adds all spinner/dropdown mappings to the Room database to use in our API calls
     */
    fun addAllMappingsToDB() {
        for(j in sortByMapStringArray.toList()) {
            val splitString = j.split(",").toTypedArray()
            sortByMapDao.insert(splitString[0],splitString[1])
        }
    }

    private fun openNowConversion(openNow: String) : String {
        return if(openNow == Constants.TRUE_OPEN_NOW_KEYWORD) "true"
        else "false"
    }
}