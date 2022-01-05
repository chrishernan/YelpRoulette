package com.example.yelproulette.IO

import com.example.yelproulette.utils.Constants
import com.example.yelproulette.retrofitDataModels.Businesses
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface YelpApiService {

    @GET("businesses/search")
    suspend fun getBusinesses(
        @Query("location") address : String,
        @Query("radius") radius : String,
        @Query("price") price : String,
        @Query("open_now") openNow : String,
        @Query("sort_by") sortBy : String,
        @Query("term") term : String
    ) : Businesses

    @GET("businesses/search")
    suspend fun getBusinessesNoCategory(
        @Query("location") address : String,
        @Query("radius") radius : String,
        @Query("price") price : String,
        @Query("open_now") openNow : String,
        @Query("sort_by") sortBy : String
    ) : Businesses

    @GET("businesses/search")
    suspend fun getBusinessesWithLongitudeLatitude(
        @Query("longitude") longitude : String,
        @Query("latitude") latitude : String,
        @Query("radius") radius : String,
        @Query("price") price : String,
        @Query("open_now") openNow : String,
        @Query("sort_by") sortBy : String,
        @Query("term") term : String
    ) : Businesses

    @GET("businesses/search")
    suspend fun getBusinessesWithLongitudeLatitudeNoCategory(
        @Query("longitude") longitude : String,
        @Query("latitude") latitude : String,
        @Query("radius") radius : String,
        @Query("price") price : String,
        @Query("open_now") openNow : String,
        @Query("sort_by") sortBy : String
    ) : Businesses


    companion object{
        fun create() : YelpApiService {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val httpClient: OkHttpClient.Builder = OkHttpClient.Builder()
            httpClient.connectTimeout(60, TimeUnit.SECONDS)
            httpClient.readTimeout(60, TimeUnit.SECONDS)
            httpClient.addInterceptor(HeaderInterceptor())
            httpClient.addInterceptor(logging)


            val retrofit= Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(Constants.YELP_API_BASE_URL)
                    .client(httpClient.build())
                    .build()

            return retrofit.create(YelpApiService::class.java)
        }
    }

    class HeaderInterceptor: Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            var request = chain.request()
                val finalToken = "Bearer "+ Constants.YELP_API_KEY
                request = request.newBuilder()
                    // dynamic token you get should be use instead of #YOUR_DYNAMIC_TOKEN.
                    .addHeader("Authorization",finalToken)
                    .build()

            return chain.proceed(request)
        }
    }
}