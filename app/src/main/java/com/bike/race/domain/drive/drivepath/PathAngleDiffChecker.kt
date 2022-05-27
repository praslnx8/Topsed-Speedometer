package com.bike.race.domain.drive.drivepath

import com.bike.race.domain.location.LocationPoint
import com.bike.race.utils.SphericalUtil
import com.google.android.gms.maps.model.LatLng
import org.koin.core.context.GlobalContext
import kotlin.math.abs

class PathAngleDiffChecker {

    private var prevLocationPoint: DrivePathItem? = null
    private var prevPrevLocationPoint: DrivePathItem? = null

    private val sphericalUtil by lazy {
        GlobalContext.get().get<SphericalUtil>()
    }

    fun addLocationPoint(locationPoint: LocationPoint, speed: Float, time: Long) {
        if (prevPrevLocationPoint == null) {
            prevPrevLocationPoint = DrivePathItemBuilder()
                .setLocationPoint(locationPoint)
                .setSpeed(speed)
                .setTime(time)
                .build()
        } else if (prevLocationPoint == null) {
            prevLocationPoint = DrivePathItemBuilder()
                .setLocationPoint(locationPoint)
                .setSpeed(speed)
                .setTime(time)
                .build()
        } else {
            val distance: Double = prevLocationPoint?.let { prevPoint ->
                sphericalUtil.computeDistanceBetween(
                    LatLng(
                        prevPoint.locationPoint.latitude,
                        prevPoint.locationPoint.longitude
                    ),
                    LatLng(locationPoint.latitude, locationPoint.longitude)
                )
            } ?: 0.0

            if (distance > 10.0) {
                prevPrevLocationPoint = prevLocationPoint
                prevLocationPoint = DrivePathItemBuilder()
                    .setLocationPoint(locationPoint)
                    .setSpeed(speed)
                    .setTime(time)
                    .build()
            }
        }
    }

    fun isAngleDiff(locationPoint: LocationPoint, isLite: Boolean): Boolean {

        val prevPrevLocPoint = prevPrevLocationPoint ?: return true
        val prevLocPoint = prevLocationPoint ?: return true

        val prevHeading = sphericalUtil.computeHeading(
            LatLng(
                prevLocPoint.locationPoint.latitude,
                prevLocPoint.locationPoint.longitude
            ),
            LatLng(
                prevPrevLocPoint.locationPoint.latitude,
                prevPrevLocPoint.locationPoint.longitude
            )
        )

        val currentHeading = sphericalUtil.computeHeading(
            LatLng(
                locationPoint.latitude,
                locationPoint.longitude
            ),
            LatLng(
                prevLocPoint.locationPoint.latitude,
                prevLocPoint.locationPoint.longitude
            )
        )

        var diff = abs(currentHeading - prevHeading)
        if (diff > 180) {
            diff = 360 - diff
        }

        return if (isLite) {
            diff > 20
        } else {
            diff > 15
        }
    }


}