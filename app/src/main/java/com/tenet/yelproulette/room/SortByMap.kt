package com.tenet.yelproulette.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SortByMap (
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "sortBy") val sortBy: String?,
    @ColumnInfo(name = "apiSortByKey") val apiSortByKey: String?
        )