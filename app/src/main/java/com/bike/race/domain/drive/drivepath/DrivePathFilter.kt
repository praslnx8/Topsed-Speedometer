package com.bike.race.domain.drive.drivepath

import com.bike.race.db.dbModels.DrivePathItemEntity
import com.bike.race.domain.location.LocationPoint
import com.bike.race.utils.SphericalUtil
import com.google.android.gms.maps.model.LatLng
import org.koin.core.context.GlobalContext

class DrivePathFilter {

    private val drivePath = mutableListOf<DrivePathItemBuilder>()

    private var topSpeed: Float = 0f

    private val sphericalUtil by lazy {
        GlobalContext.get().get<SphericalUtil>()
    }

    private val pathAngleDiffChecker by lazy {
        GlobalContext.get().get<PathAngleDiffChecker>()
    }

    private val speedDiffCalculator by lazy {
        GlobalContext.get().get<SpeedDiffCalculator>()
    }

    fun filter(drivePaths: List<DrivePathItemEntity>): List<DrivePathItem> {
        drivePaths.mapIndexed { index, drivePathItem ->
            addRacePathItem(
                drivePathItem,
                index == 0 || index == drivePaths.size - 1
            )
        }

        return drivePath.map {
            it.build()
        }
    }

    private fun addRacePathItem(
        drivePathItem: DrivePathItemEntity,
        forceAdd: Boolean = false
    ) {
        if (isCriteriaMatches(LocationPoint(drivePathItem.lat, drivePathItem.lon), drivePathItem.speed) || forceAdd) {
            drivePath.add(
                DrivePathItemBuilder()
                    .setLocationPoint(LocationPoint(drivePathItem.lat, drivePathItem.lon))
                    .setSpeed(drivePathItem.speed)
                    .setTime(drivePathItem.time)
            )
            preComputeRacePathDetails()
        }
        pathAngleDiffChecker.addLocationPoint(
            LocationPoint(drivePathItem.lat, drivePathItem.lon),
            drivePathItem.speed,
            drivePathItem.time
        )
        updateTopSpeed(topSpeed)
    }

    private fun preComputeRacePathDetails() {
        val racePathPrev = drivePath.getOrNull(drivePath.size - 2) ?: return
        val currentRacePath = drivePath.getOrNull(drivePath.size - 1) ?: return

        val distance = sphericalUtil.computeDistanceBetween(
            LatLng(
                racePathPrev.getLocationPoint().latitude,
                racePathPrev.getLocationPoint().longitude
            ),
            LatLng(
                currentRacePath.getLocationPoint().latitude,
                currentRacePath.getLocationPoint().longitude
            )
        ).toInt()

        val duration = currentRacePath.getTime() - racePathPrev.getTime()

        racePathPrev
            .setDistance(distance)
            .setDuration(duration)
            .setNextLocationPoint(currentRacePath.getLocationPoint())
    }

    private fun isCriteriaMatches(
        locationPoint: LocationPoint,
        speed: Float,
    ): Boolean {
        val prevLocationPoint = drivePath.getOrNull(drivePath.size - 1)
        return drivePath.isEmpty() ||
                isStop(speed, prevLocationPoint) ||
                pathAngleDiffChecker.isAngleDiff(locationPoint, false) ||
                speedDiffCalculator.isSpeedDiff(topSpeed, speed, prevLocationPoint?.getSpeed())
    }

    private fun isStop(speed: Float, prevDrivePathItem: DrivePathItemBuilder?): Boolean {
        val prevSpeed = prevDrivePathItem?.getSpeed() ?: return false
        return prevSpeed > 0.1f && speed < 0.1f
    }

    private fun updateTopSpeed(speed: Float) {
        if (speed > topSpeed) {
            topSpeed = speed
        }
    }
}