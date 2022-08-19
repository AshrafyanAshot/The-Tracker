package com.ash.thetracker.data.repository.trackerRepository

import androidx.lifecycle.LiveData
import com.ash.thetracker.presentation.model.TrackingModel

interface ITrackingRepository {

    suspend fun insert(trackingEntity: TrackingModel)
    suspend fun getLastTrackingEntityRecord(): TrackingModel?
    suspend fun getAllTrackingModels(): List<TrackingModel>
    suspend fun deleteAll()

    fun getAllTrackingModelsInLive(): LiveData<List<TrackingModel>>
    fun getTotalDistanceTravelledInLive(): LiveData<Float?>
    fun getLastTrackingEntityInLive(): LiveData<TrackingModel?>
}
