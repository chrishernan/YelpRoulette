package com.example.yelproulette.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CategoryMap(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "category") val category: String?,
    @ColumnInfo(name = "apiCategoryName") val apiCategoryName: String?
    )
