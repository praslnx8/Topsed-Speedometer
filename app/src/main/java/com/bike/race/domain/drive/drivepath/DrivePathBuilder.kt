package com.bike.race.domain.drive.drivepath

import com.bike.race.domain.location.LocationPoint
import org.koin.core.context.GlobalContext

class DrivePathBuilder {

    private val drivePath = mutableListOf<DrivePathItemBuilder>()
    private val drivePathLite = mutableListOf<LocationPoint>()

    private val pathAngleDiffChecker by lazy {
        GlobalContext.get().get<PathAngleDiffChecker>()
    }

    fun addRacePathItem(
        locationPoint: LocationPoint,
        speed: Float,
        time: Long,
        forceAdd: Boolean = false
    ) {
        drivePath.add(
            DrivePathItemBuilder()
                .setLocationPoint(locationPoint)
                .setSpeed(speed)
                .setTime(time)
        )
        if (isCriteriaMatchesForLite(locationPoint) || forceAdd) {
            drivePathLite.add(locationPoint)
        }
    }

    fun getRacePath(): List<DrivePathItem> = drivePath.map {
        it.build()
    }.toList()

    fun getRacePathLite(): List<LocationPoint> = drivePathLite.toList()

    private fun isCriteriaMatchesForLite(locationPoint: LocationPoint): Boolean {
        return pathAngleDiffChecker.isAngleDiff(locationPoint, true)
    }
}