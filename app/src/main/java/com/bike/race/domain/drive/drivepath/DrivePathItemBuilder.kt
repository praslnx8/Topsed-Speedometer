package com.bike.race.domain.drive.drivepath

import com.bike.race.domain.location.LocationPoint

class DrivePathItemBuilder {

    private lateinit var locationPoint: LocationPoint
    private var speed: Float = 0f
    private var time: Long = 0
    private var duration: Long = 0
    private var distance: Int = 0
    private var nextLocationPoint: LocationPoint? = null

    fun setLocationPoint(locationPoint: LocationPoint): DrivePathItemBuilder {
        this.locationPoint = locationPoint
        return this
    }

    fun setSpeed(speed: Float): DrivePathItemBuilder {
        this.speed = speed
        return this
    }

    fun setTime(time: Long): DrivePathItemBuilder {
        this.time = time
        return this
    }

    fun setDuration(duration: Long): DrivePathItemBuilder {
        this.duration = duration
        return this
    }

    fun setDistance(distance: Int): DrivePathItemBuilder {
        this.distance = distance
        return this
    }

    fun setNextLocationPoint(locationPoint: LocationPoint): DrivePathItemBuilder {
        this.nextLocationPoint = locationPoint
        return this
    }

    fun build(): DrivePathItem {
        return DrivePathItem(
            locationPoint = locationPoint,
            speed = speed,
            time = time,
            duration = duration,
            distance = distance,
            nextLocationPoint = nextLocationPoint
        )
    }

    fun getLocationPoint(): LocationPoint = locationPoint

    fun getTime() = time

    fun getSpeed() = speed
}