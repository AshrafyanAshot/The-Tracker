package com.ash.thetracker.data.model.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ash.thetracker.presentation.model.TrackingModel

@Entity(tableName = "tracking_entity")
data class TrackingEntity(
    @PrimaryKey val timestamp: Long,
    @ColumnInfo val latitude: Double,
    @ColumnInfo val longitude: Double,
    @ColumnInfo var distanceTravelled: Float = 0f
) {
    fun toModel() = TrackingModel(timestamp = timestamp, latitude = latitude, longitude = longitude, distanceTravelled = distanceTravelled)
}
