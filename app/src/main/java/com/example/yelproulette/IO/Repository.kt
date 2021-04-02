package com.example.yelproulette.IO

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.yelproulette.DependencyInjection.AppModule
import com.example.yelproulette.retrofitDataModels.Businesses
import com.example.yelproulette.retrofitDataModels.BusinessesItem
import com.example.yelproulette.room.ApiMapDatabase
import com.example.yelproulette.room.CategoryMapDao
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
    private val categoryMapDao: CategoryMapDao,
    private val sortByMapDao: SortByMapDao,
    @AppModule.CategoryMapStringArray  private val categoryMapStringArray: Array<String>,
    @AppModule.SortByMapStringArray private val sortByMapStringArray: Array<String>) {

    //Initializing coroutine variables
    private var isDatabasePopulating = false
    val cpuScope = CoroutineScope(Dispatchers.Default)
    private val ioCoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, exception ->
        Timber.e("Exception Thrown => $exception")
    }

    /**
     * Fetches businesses when category is specifed (not equal to "Any")
     * TODO add way to catch if we get back 0 results from request
     */
    suspend fun fetchCategoryBusiness(address : String,
                                      radius : String,
                                      price : String,
                                      openNow : String,
                                      sortBy : String,
                                      categories : String) : BusinessesItem? {
        //If categories is set to Any call different function to handle that
        if(categories == Constants.ALL_CATEGORY_SPINNER_KEYWORD) {
            return fetchNoCategoryBusiness(
                address,
                radius,
                price,
                openNow,
                sortBy,
                categories)
        }
        //else, if there is a category, handle in this function
        else {
            val businesses = yelpApiHelper.getBusinesses(
                    address,
                    convertMilesToMeters(radius.toInt()).toString(),
                    price.length.toString(),
                    openNowConversion(openNow),
                    sortByMapDao.getApiSortByKey(sortBy),
                    categoryMapDao.getApiCategoryName(categories))

            //Computes random business from return businesses
            val randomBusinessJob  = cpuScope.async { randomBusiness(businesses)}
            val randomBus = randomBusinessJob.await()
            return randomBus
        }

    }

    /**
     * Fetches businesses when category is set to "Any"
     */
    suspend fun fetchNoCategoryBusiness(address : String,
                                        radius : String,
                                        price : String,
                                        openNow : String,
                                        sortBy : String,
                                        categories : String) : BusinessesItem? {
        val businesses = yelpApiHelper.getBusinessesNoCategory(
            address,
            convertMilesToMeters(radius.toInt()).toString(),
            price.length.toString(),
            openNowConversion(openNow),
            sortByMapDao.getApiSortByKey(sortBy))

        //Computes random business from return businesses
        val randomBusinessJob  = cpuScope.async { randomBusiness(businesses)}
        val randomBus = randomBusinessJob.await()
        return randomBus
    }

    /**
     * Fetches category business with Latitude and Longitude
     */
    suspend fun fetchCategoryLatitudeLongitudeBusiness(longitude : String,
                                                       latitude : String,
                                                       radius : String,
                                                       price : String,
                                                       openNow : String,
                                                       sortBy : String,
                                                       categories : String): BusinessesItem? {
        if(categories == Constants.ALL_CATEGORY_SPINNER_KEYWORD) {
            return fetchNoCategoryLatitudeLongitudeBusiness(
                longitude,
                latitude,
                radius,
                price,
                openNow,
                sortBy)
        } else {
            Timber.e(" Repository => ${sortByMapDao.getApiSortByKey(sortBy)}")
            val businesses = yelpApiHelper.getBusinessesWithLongitudeLatitude(
                    longitude,
                    latitude,
                    convertMilesToMeters(radius.toInt()).toString(),
                    price.length.toString(),
                    openNowConversion(openNow),
                    sortByMapDao.getApiSortByKey(sortBy),
                    categoryMapDao.getApiCategoryName(categories))

            //Computes random business from return businesses
            val randomBusinessJob  = cpuScope.async { randomBusiness(businesses)}
            val randomBus = randomBusinessJob.await()
            return randomBus
        }

    }

    /**
     * Fetches any business with Latitude and Longitude when category is set to "Any"
     */
    suspend fun fetchNoCategoryLatitudeLongitudeBusiness(longitude : String,
                                                         latitude : String,
                                                         radius : String,
                                                         price : String,
                                                         openNow : String,
                                                         sortBy : String) : BusinessesItem? {
        val businesses = yelpApiHelper.getBusinessesWithLongitudeLatitudeNoCategory(
            longitude,
            latitude,
            convertMilesToMeters(radius.toInt()).toString(),
            price.length.toString(),
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
            for(i in 0 until businesses.businesses?.size!!) {
                Timber.e("Business $i => ${businesses.businesses[i]?.name}")
            }
            Timber.e("Random business => ${randomBusiness?.name}")
            randomBusiness
        } else{
            null
        }
    }

    /**
     * Adds all spinner/dropdown mappings to the Room database to use in our API calls
     */
    fun addAllMappingsToDB() {
        for(i in categoryMapStringArray.toList()) {
            val splitString = i.split(",").toTypedArray()
            categoryMapDao.insert(splitString[0],splitString[1])
        }

        for(j in sortByMapStringArray.toList()) {
            val splitString = j.split(",").toTypedArray()
            sortByMapDao.insert(splitString[0],splitString[1])
        }
        Timber.e("Room DB Populated")
    }

    private fun openNowConversion(openNow: String) : String {
        return if(openNow == Constants.TRUE_OPEN_NOW_KEYWORD) "true"
        else "false"
    }
}