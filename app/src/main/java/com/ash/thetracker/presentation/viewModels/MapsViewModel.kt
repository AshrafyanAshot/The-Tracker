package com.ash.thetracker.presentation.viewModels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ash.thetracker.data.repository.trackerRepository.ITrackingRepository
import com.ash.thetracker.presentation.model.TrackingModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MapsViewModel(private val trackingRepository: ITrackingRepository) : ViewModel() {

    val allTrackingModelsLiveData: MediatorLiveData<List<TrackingModel>> = MediatorLiveData()
    val lastTrackingModelLiveData: MediatorLiveData<TrackingModel?> = MediatorLiveData()
    val totalDistanceTravelledLiveData: MediatorLiveData<Float?> = MediatorLiveData()

    val allTrackingModels: MutableLiveData<List<TrackingModel>> = MutableLiveData(listOf())
    val currentNumberOfStepCount = MutableLiveData(0)
    var initialStepCount = 0

    init {
        subscribeToAllTrackingModels()
        subscribeToLastTrackingModel()
        subscribeToTotalDistanceTraveled()
    }

    private fun subscribeToAllTrackingModels() {
        viewModelScope.launch(Dispatchers.IO) {
            val allTrackingModels = trackingRepository.getAllTrackingModelsInLive()
            allTrackingModelsLiveData.addSource(allTrackingModels) { models ->
                allTrackingModelsLiveData.postValue(models)
            }
        }
    }

    private fun subscribeToLastTrackingModel() {
        viewModelScope.launch(Dispatchers.IO) {
            val lasTrackingModel = trackingRepository.getLastTrackingEntityInLive()
            allTrackingModelsLiveData.addSource(lasTrackingModel) { model ->
                lastTrackingModelLiveData.postValue(model)
            }
        }
    }

    private fun subscribeToTotalDistanceTraveled() {
        viewModelScope.launch(Dispatchers.IO) {
            val totalDistanceTraveled = trackingRepository.getTotalDistanceTravelledInLive()
            totalDistanceTravelledLiveData.addSource(totalDistanceTraveled) { distance ->
                totalDistanceTravelledLiveData.postValue(distance)
            }
        }
    }

    fun getAllTrackingModels() {
        viewModelScope.launch(Dispatchers.IO) {
            allTrackingModels.postValue(trackingRepository.getAllTrackingModels())
        }
    }

    fun insertTrackingModel(trackingModel: TrackingModel) {
        viewModelScope.launch(Dispatchers.IO) {
            trackingRepository.getLastTrackingEntityRecord()?.let {
                trackingModel.distanceTravelled = trackingModel.distanceTo(it)
            }
            trackingRepository.insert(trackingModel)
        }
    }

    fun deleteAllTrackingModels() {
        viewModelScope.launch(Dispatchers.IO) {
            currentNumberOfStepCount.postValue(0)
            initialStepCount = 0
            trackingRepository.deleteAll()
        }
    }
}
