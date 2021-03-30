package com.example.yelproulette.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SortByMapDao {
    @Query("SELECT * FROM SortByMap")
    fun getAll() : List<SortByMap>

    @Query("SELECT apiSortByKey FROM SortByMap WHERE sortBy =:sortBy ")
    fun getApiSortByKey(sortBy : String) : String

    @Query("INSERT INTO SortByMap (sortBy,apiSortByKey) VALUES (:sortBy,:apiSortByKey)")
    fun insert(sortBy : String, apiSortByKey : String)

    @Insert
    fun insertAll(vararg sortByMaps : SortByMap)

    @Delete
    fun delete(sortByMap: SortByMap )

}