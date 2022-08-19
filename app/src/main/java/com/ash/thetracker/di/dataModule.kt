package com.ash.thetracker.di

import androidx.room.Room
import com.ash.thetracker.App
import com.ash.thetracker.data.AppDataBase
import com.ash.thetracker.data.dao.TrackingDao
import com.ash.thetracker.data.repository.trackerRepository.ITrackingRepository
import com.ash.thetracker.data.repository.trackerRepository.TrackingRepositoryImpl
import kotlinx.serialization.ExperimentalSerializationApi
import org.koin.dsl.module

private const val CONTENT_TYPE = "application/json"

@ExperimentalSerializationApi
val dataModule = module {

    /**App component's*/
    single {
        Room.databaseBuilder(
            App.applicationContext(),
            AppDataBase::class.java, "app_db"
        ).build()
    }

    /**Dao*/
    single<TrackingDao> { get<AppDataBase>().trackingDao() }

    /**Repository*/
    single<ITrackingRepository> {
        TrackingRepositoryImpl(get<TrackingDao>())
    }
}