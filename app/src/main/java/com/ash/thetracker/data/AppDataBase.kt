package com.ash.thetracker.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ash.thetracker.data.dao.TrackingDao
import com.ash.thetracker.data.model.entity.TrackingEntity

@Database(
    entities = [TrackingEntity::class], version = 1, exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {
    abstract fun trackingDao(): TrackingDao
}