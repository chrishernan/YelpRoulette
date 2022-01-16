package com.tenet.yelproulette.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = arrayOf(CategoryMap::class,SortByMap::class),version = 2)
abstract class ApiMapDatabase : RoomDatabase(){
    abstract fun categoryMapDao() : CategoryMapDao
    abstract fun sortByMapDao() : SortByMapDao
}

val MIGRATION_1_2 : Migration = object : Migration(1,2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE SortByMap" +
                "(id INTEGER NOT NULL, sortBy TEXT, apiSortKey TEXT, PRIMARY KEY(id)")
    }
}