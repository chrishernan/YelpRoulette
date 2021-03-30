package com.example.yelproulette.IO

import retrofit2.http.Query
import javax.inject.Inject

class YelpApiHelper @Inject constructor(private val yelpApiService : YelpApiService) {

    suspend fun getBusinesses(
        address : String,
        radius : String,
        price : String,
        openNow : String,
        sortBy : String,
        categories : String
    ) = yelpApiService.getBusinesses(
        address, radius, price, openNow, sortBy, categories
    )

    suspend fun getBusinessesNoCategory(
        address : String,
        radius : String,
        price : String,
        openNow : String,
        sortBy : String
    ) = yelpApiService.getBusinessesNoCategory(
        address, radius, price, openNow, sortBy
    )


    suspend fun getBusinessesWithLongitudeLatitude(
        longitude : String,
        latitude : String,
        radius : String,
        price : String,
        openNow : String,
        sortBy : String,
        categories : String
    ) = yelpApiService.getBusinessesWithLongitudeLatitude(
        longitude,latitude, radius, price, openNow, sortBy, categories
    )

    suspend fun getBusinessesWithLongitudeLatitudeNoCategory(
        longitude : String,
        latitude : String,
        radius : String,
        price : String,
        openNow : String,
        sortBy : String
    ) = yelpApiService.getBusinessesWithLongitudeLatitudeNoCategory(
        longitude,latitude, radius, price, openNow, sortBy
    )

}