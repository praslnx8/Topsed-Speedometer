package com.bike.race.domain.drive

import com.bike.race.domain.drive.drivepath.DrivePathItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class DriveStatAnalyser {


    suspend fun getDriveStatsData(path: List<DrivePathItem>): DriveStatsData {

        return withContext(Dispatchers.Default) {

            var noOfStops = 0
            var idleTime = 0L

            var prevSpeed = 0f
            path.forEach { item ->
                if (item.speed == 0f && prevSpeed > 0f) {
                    noOfStops++
                    idleTime += item.duration
                }
                prevSpeed = item.speed
            }

            DriveStatsData(
                noOfStops = noOfStops,
                idleTime = idleTime
            )
        }
    }

    suspend fun getDriveSpeedMap(path: List<DrivePathItem>): Map<Long, Double> {
        return withContext(Dispatchers.Default) {
            val speedMap = mutableMapOf<Long, Double>()
            var prevTime = 0L

            path.forEach { item ->
                if (TimeUnit.MILLISECONDS.toMinutes(item.time) > prevTime) {
                    prevTime = TimeUnit.MILLISECONDS.toMinutes(item.time)
                    speedMap[item.time] = item.speed.toDouble()
                }
            }

            speedMap
        }
    }

    data class DriveStatsData(
        val noOfStops: Int,
        val idleTime: Long
    )
}