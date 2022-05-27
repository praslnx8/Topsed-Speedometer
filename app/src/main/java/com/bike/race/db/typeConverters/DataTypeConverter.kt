package com.bike.race.db.typeConverters

import androidx.room.TypeConverter
import com.bike.race.domain.drive.drivepath.DrivePathItem
import com.bike.race.domain.location.LocationPoint
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DataTypeConverter {

    @TypeConverter
    fun fromLocationPoint(locationPoint: LocationPoint): String = Gson().toJson(locationPoint)

    @TypeConverter
    fun toLocationPoint(locPointStr: String): LocationPoint =
        Gson().fromJson(locPointStr, LocationPoint::class.java)

    @TypeConverter
    fun fromDrivePathItem(drivePathItem: DrivePathItem): String = Gson().toJson(drivePathItem)

    @TypeConverter
    fun toDrivePathItem(racePathStr: String): DrivePathItem =
        Gson().fromJson(racePathStr, DrivePathItem::class.java)

    @TypeConverter
    fun fromDrivePathItems(drivePathItems: List<DrivePathItem>): String =
        Gson().toJson(drivePathItems)

    @TypeConverter
    fun toDrivePathItems(drivePathStr: String): List<DrivePathItem> {
        val drivePathItemType = object : TypeToken<List<DrivePathItem>>() {}.type
        return Gson().fromJson(drivePathStr, drivePathItemType)
    }

    @TypeConverter
    fun fromLocPath(locPath: List<LocationPoint>): String = Gson().toJson(locPath)

    @TypeConverter
    fun toLocPath(locPathStr: String): List<LocationPoint> {
        val locationPointsType = object : TypeToken<List<LocationPoint>>() {}.type
        return Gson().fromJson(locPathStr, locationPointsType)
    }
}