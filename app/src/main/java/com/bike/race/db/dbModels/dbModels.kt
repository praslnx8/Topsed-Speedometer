package com.bike.race.db.dbModels

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bike.race.domain.location.LocationPoint

@Entity(tableName = "DRIVE")
data class DriveEntity(
    @PrimaryKey(autoGenerate = true) val id: Long? = null,
    @ColumnInfo(name = "START_LOCATION") val startLocationPoint: LocationPoint,
    @ColumnInfo(name = "START_LOCALITY") val startLocality: String?,
    @ColumnInfo(name = "END_LOCATION") val endLocationPoint: LocationPoint,
    @ColumnInfo(name = "END_LOCALITY") val endLocality: String?,
    @ColumnInfo(name = "START_TIME") val startTime: Long,
    @ColumnInfo(name = "PAUSE_TIME") val pauseTime: Long,
    @ColumnInfo(name = "END_TIME") val endTime: Long,
    @ColumnInfo(name = "DISTANCE") val distance: Int,
    @ColumnInfo(name = "TOP_SPEED") val topSpeed: Double,
    @ColumnInfo(name = "LOCATION_PATH") val locationPath: List<LocationPoint>,
    @ColumnInfo(name = "IS_MOCK_DRIVE") val isMockDrive: Boolean = false,
    @ColumnInfo(name = "TAG") val tag: String? = null,
    @ColumnInfo(name = "SYNCED_ID") val syncedId: Long? = null,
    @ColumnInfo(name = "CREATED_AT") val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "DRIVE_PATH_ITEM")
data class DrivePathItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "drive_id") val driveId: Long,
    @ColumnInfo(name = "lat") val lat: Double,
    @ColumnInfo(name = "lon") val lon: Double,
    @ColumnInfo(name = "speed") val speed: Float,
    @ColumnInfo(name = "time") val time: Long,
    @ColumnInfo(name = "duration") val duration: Long,
    @ColumnInfo(name = "distance") val distance: Int,
    @ColumnInfo(name = "sequence") val sequence: Int
)