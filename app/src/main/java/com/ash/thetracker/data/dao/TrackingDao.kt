package com.ash.thetracker.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ash.thetracker.data.model.entity.TrackingEntity

@Dao
interface TrackingDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(trackingEntity: TrackingEntity)

    @Query("SELECT * FROM tracking_entity ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastTrackingEntityRecord(): TrackingEntity?

    @Query("SELECT * FROM tracking_entity")
    suspend fun getAllTrackingEntitiesRecord(): List<TrackingEntity>

    @Query("DELETE FROM tracking_entity")
    suspend fun deleteAll()

    @Query("SELECT * FROM tracking_entity")
    fun getAllTrackingEntitiesInLive(): LiveData<List<TrackingEntity>>

    @Query("SELECT SUM(distanceTravelled) FROM tracking_entity")
    fun getTotalDistanceTravelledInLive(): LiveData<Float?>

    @Query("SELECT * FROM tracking_entity ORDER BY timestamp DESC LIMIT 1")
    fun getLastTrackingEntityInLive(): LiveData<TrackingEntity?>
}