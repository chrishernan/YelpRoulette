package com.example.yelproulette.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CategoryMapDao {
    @Query("SELECT * FROM CategoryMap")
    fun getAll() : List<CategoryMap>

    @Query("SELECT apiCategoryName FROM CategoryMap WHERE category =:category ")
    fun getApiCategoryName(category : String) : String

    @Query("INSERT INTO CategoryMap (category,apiCategoryName) VALUES (:category,:apiCategoryName)")
    fun insert(category : String, apiCategoryName : String)

    @Insert
    fun insertAll(vararg categoryMaps : CategoryMap)

    @Delete
    fun delete(categoryMap : CategoryMap)

}