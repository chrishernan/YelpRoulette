package com.example.yelproulette.utils

object Constants {

    //Yelp API Constants
    const val YELP_API_BASE_URL = "https://api.yelp.com/v3/"

    //todo build server and hold key in server instead of distributing this key to every app download
    const val YELP_API_KEY = "yM60J5gKrlBlkwCtDlobQptfx93QDPgyUG9JDEcFuPkzxiH0H6ihT75PHqAERR7D-WnN3SJ9jSANsuOY9rzhZ9C70f3WV0mzCL0mJ7CIB7pO4X2BLQxoy_ZdEjRQYHYx"

    // Important Spinner values
    const val ALL_CATEGORY_SPINNER_KEYWORD = "Any"
    const val TRUE_OPEN_NOW_KEYWORD = "Yes"
    const val FALSE_OPEN_NOW_KEYWORD = "No"

    //Filepath to file containing category mappings
    const val CATEGORY_MAPPING_FILE_PATH = "values/categories_map_array.xml"

    //Constant Numbers used for conversion
    const val METER_TO_MILE = 1609

    //GoogleMaps/Places API
    const val STREET_ZOOM_LEVEL = 15.0F
    const val GOOGLE_MAPS_NAVIGATION_PREFIX = "+"
    const val GOOGLE_MAPS_NAVIGATION_SEPARATOR = "+"
}