package com.ash.thetracker.data.repository.trackerRepository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.ash.thetracker.data.dao.TrackingDao
import com.ash.thetracker.presentation.model.TrackingModel

class TrackingRepositoryImpl(private val dao: TrackingDao) : ITrackingRepository {

    override suspend fun insert(trackingEntity: TrackingModel) {
        dao.insert(trackingEntity.toEntityModel())
    }

    override suspend fun getLastTrackingEntityRecord(): TrackingModel? {
        return dao.getLastTrackingEntityRecord()?.toModel()
    }

    override suspend fun getAllTrackingModels(): List<TrackingModel> {
        return dao.getAllTrackingEntitiesRecord().map { it.toModel() }
    }

    override suspend fun deleteAll() {
        dao.deleteAll()
    }

    override fun getAllTrackingModelsInLive(): LiveData<List<TrackingModel>> {
        return dao.getAllTrackingEntitiesInLive().map { it.map { entity -> entity.toModel() } }
    }

    override fun getTotalDistanceTravelledInLive(): LiveData<Float?> {
        return dao.getTotalDistanceTravelledInLive()
    }

    override fun getLastTrackingEntityInLive(): LiveData<TrackingModel?> {
        return dao.getLastTrackingEntityInLive().map { entity -> entity?.toModel() }
    }
}