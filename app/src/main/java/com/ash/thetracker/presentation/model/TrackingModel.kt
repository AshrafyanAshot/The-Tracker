package com.ash.thetracker.presentation.model

import android.location.Location
import com.ash.thetracker.data.model.entity.TrackingEntity
import com.google.android.gms.maps.model.LatLng

data class TrackingModel(
    val timestamp: Long,
    val latitude: Double,
    val longitude: Double,
    var distanceTravelled: Float = 0f
) {
    fun toEntityModel() = TrackingEntity(timestamp = timestamp, latitude = latitude, longitude = longitude, distanceTravelled = distanceTravelled)

    fun asLatLng() = LatLng(latitude, longitude)

    fun distanceTo(newTrackingModel:TrackingModel): Float {
        val locationA = Location("Previous Location")
        locationA.latitude = latitude
        locationA.longitude = longitude

        val locationB = Location("New Location")
        locationB.latitude = newTrackingModel.latitude
        locationB.longitude = newTrackingModel.longitude

        return locationA.distanceTo(locationB)
    }
}