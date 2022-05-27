package com.bike.race.domain.drive

import com.bike.race.domain.location.LocationPoint
import java.util.concurrent.TimeUnit

data class Drive(
    val id: Long? = null,
    val tag: String? = null,
    val startLocation: LocationPoint,
    var startLocality: String? = null,
    val endLocation: LocationPoint,
    var endLocality: String? = null,
    val distance: Int,
    val startTime: Long,
    val endTime: Long,
    val pauseTime: Long,
    val topSpeed: Double,
    val locationPath: List<LocationPoint>,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getTotalTime() = (endTime - startTime) - pauseTime

    fun getId() = id ?: throw IllegalAccessException("id accessed before storing")

    fun getAvgSpeed(): Double {
        return if (getTotalTime() > 0) {
            (distance / TimeUnit.MILLISECONDS.toSeconds(getTotalTime())).toDouble()
        } else {
            0.0
        }
    }
}