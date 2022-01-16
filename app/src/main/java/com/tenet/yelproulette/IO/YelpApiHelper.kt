package com.tenet.yelproulette.IO

import retrofit2.http.Query
import javax.inject.Inject

class YelpApiHelper @Inject constructor(private val yelpApiService : YelpApiService) {

    suspend fun getBusinesses(
        address : String,
        radius : String,
        price : String,
        openNow : String,
        sortBy : String,
        term : String
    ) = yelpApiService.getBusinesses(
        address, radius, price, openNow, sortBy, term
    )

    suspend fun getBusinessesNoTerm(
        address : String,
        radius : String,
        price : String,
        openNow : String,
        sortBy : String
    ) = yelpApiService.getBusinessesNoTerm(
        address, radius, price, openNow, sortBy
    )


    suspend fun getBusinessesWithLongitudeLatitude(
        longitude : String,
        latitude : String,
        radius : String,
        price : String,
        openNow : String,
        sortBy : String,
        term : String
    ) = yelpApiService.getBusinessesWithLongitudeLatitude(
        longitude,latitude, radius, price, openNow, sortBy, term
    )

    suspend fun getBusinessesWithLongitudeLatitudeNoTerm(
        longitude : String,
        latitude : String,
        radius : String,
        price : String,
        openNow : String,
        sortBy : String
    ) = yelpApiService.getBusinessesWithLongitudeLatitudeNoTerm(
        longitude,latitude, radius, price, openNow, sortBy
    )

}