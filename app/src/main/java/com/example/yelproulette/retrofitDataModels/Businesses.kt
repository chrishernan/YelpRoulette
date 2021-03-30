package com.example.yelproulette.retrofitDataModels

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Businesses(

	@field:SerializedName("total")
	val total: Int? = null,

	@field:SerializedName("region")
	val region: Region? = null,

	@field:SerializedName("businesses")
	val businesses: List<BusinessesItem?>? = null
) : Parcelable

@Parcelize
data class Location(

	@field:SerializedName("country")
	val country: String? = null,

	@field:SerializedName("address3")
	val address3: String? = null,

	@field:SerializedName("address2")
	val address2: String? = null,

	@field:SerializedName("city")
	val city: String? = null,

	@field:SerializedName("address1")
	val address1: String? = null,

	@field:SerializedName("display_address")
	val displayAddress: List<String?>? = null,

	@field:SerializedName("state")
	val state: String? = null,

	@field:SerializedName("zip_code")
	val zipCode: String? = null
) : Parcelable

@Parcelize
data class Region(

	@field:SerializedName("center")
	val center: Center? = null
) : Parcelable

@Parcelize
data class CategoriesItem(

	@field:SerializedName("alias")
	val alias: String? = null,

	@field:SerializedName("title")
	val title: String? = null
) : Parcelable

@Parcelize
data class BusinessesItem(

	@field:SerializedName("distance")
	val distance: Double? = null,

	@field:SerializedName("image_url")
	val imageUrl: String? = null,

	@field:SerializedName("rating")
	val rating: Double? = null,

	@field:SerializedName("coordinates")
	val coordinates: Coordinates? = null,

	@field:SerializedName("review_count")
	val reviewCount: Int? = null,

	@field:SerializedName("transactions")
	val transactions: List<String?>? = null,

	@field:SerializedName("url")
	val url: String? = null,

	@field:SerializedName("display_phone")
	val displayPhone: String? = null,

	@field:SerializedName("phone")
	val phone: String? = null,

	@field:SerializedName("price")
	val price: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("alias")
	val alias: String? = null,

	@field:SerializedName("location")
	val location: Location? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("categories")
	val categories: List<CategoriesItem?>? = null,

	@field:SerializedName("is_closed")
	val isClosed: Boolean? = null
) : Parcelable

@Parcelize
data class Center(

	@field:SerializedName("latitude")
	val latitude: Double? = null,

	@field:SerializedName("longitude")
	val longitude: Double? = null
) : Parcelable

@Parcelize
data class Coordinates(

	@field:SerializedName("latitude")
	val latitude: Double? = null,

	@field:SerializedName("longitude")
	val longitude: Double? = null
) : Parcelable
