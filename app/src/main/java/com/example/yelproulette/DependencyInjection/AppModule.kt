package com.example.yelproulette.DependencyInjection

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import com.example.yelproulette.IO.YelpApiHelper
import com.example.yelproulette.IO.YelpApiService
import com.example.yelproulette.R
import com.example.yelproulette.room.ApiMapDatabase
import com.example.yelproulette.room.MIGRATION_1_2
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Singleton
    @Provides
    fun bindYelpApiService() : YelpApiHelper {
        return YelpApiHelper(YelpApiService.create())
    }

    @Singleton
    @Provides
    fun provideCategoryDatabase(
        @ApplicationContext app : Context
    ) : ApiMapDatabase
    {
        return Room.databaseBuilder(
            app,
            ApiMapDatabase::class.java, "ApiMapDatabase")
                .addMigrations(MIGRATION_1_2)
                .build()

    }

    @Singleton
    @Provides
    fun provideCategoryDao(db : ApiMapDatabase) = db.categoryMapDao()

    @Singleton
    @Provides
    fun provideSortByDao(db : ApiMapDatabase) = db.sortByMapDao()

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class CategoryMapStringArray

    @CategoryMapStringArray
    @Provides
    fun providesCategoryMapStringArray(@ApplicationContext app: Context) : Array<String> {
        return app.resources.getStringArray(R.array.categories_map_array)
    }

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class SortByMapStringArray

    @SortByMapStringArray
    @Provides
    fun providesSortByMapStringArray(@ApplicationContext app: Context) : Array<String> {
        return app.resources.getStringArray(R.array.sort_by_map_array)
    }
}