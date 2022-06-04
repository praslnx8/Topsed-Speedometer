package com.bike.race.domain.drive.currentDrive

import com.bike.race.domain.location.LocationPoint
import com.bike.race.utils.SphericalUtil
import com.bike.race.utils.toLatLng
import org.koin.core.context.GlobalContext
import java.util.concurrent.TimeUnit

/**
 * This is created to avoid including data points from GPS in case if the user forgot to end the drive
 * after reaching the destination and being idle for a long time.
 */
class EndForgotCalculator {

    private val sphericalUtil by lazy {
        GlobalContext.get().get<SphericalUtil>()
    }

    private val locationPoints: MutableMap<Long, LocationPoint> =
        object : LinkedHashMap<Long, LocationPoint>() {
            override fun removeEldestEntry(eldest: MutableMap.MutableEntry<Long, LocationPoint>?): Boolean {
                return size > 4
            }
        }

    fun addPoint(locationTime: Long, locationPoint: LocationPoint) {
        locationPoints[TimeUnit.MILLISECONDS.toSeconds(locationTime)] = locationPoint
    }


    fun isForgot(speed: Float): Boolean {
        val locationPointList = locationPoints.values.toList()

        val locationPoint1 = locationPointList.getOrNull(0)
        val locationPoint2 = locationPointList.getOrNull(1)
        val locationPoint3 = locationPointList.getOrNull(2)
        val locationPoint4 = locationPointList.getOrNull(3)

        return speed <= 1f && isDiffSmall(locationPoint1, locationPoint2) &&
                isDiffSmall(locationPoint1, locationPoint3) &&
                isDiffSmall(locationPoint1, locationPoint4) &&
                isDiffSmall(locationPoint2, locationPoint3) &&
                isDiffSmall(locationPoint2, locationPoint4)
    }

    private fun isDiffSmall(
        locationPoint1: LocationPoint?,
        locationPoint2: LocationPoint?
    ): Boolean {
        if (locationPoint1 == null || locationPoint2 == null) {
            return false
        }

        return sphericalUtil.computeDistanceBetween(
            locationPoint1.toLatLng(),
            locationPoint2.toLatLng()
        ) <= 2
    }
}