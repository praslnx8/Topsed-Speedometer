package com.bike.race.uiModels

import com.bike.race.utils.ConversionUtil
import org.koin.core.context.GlobalContext
import java.util.concurrent.TimeUnit

data class DriveReportData(
    private val topSpeed: Double,
    private val distance: Int,
    private val startTime: Long,
    private val endTime: Long,
    private val pausedTime: Long
) {

    private val conversionUtil by lazy {
        GlobalContext.get().get<ConversionUtil>()
    }

    fun getTopSpeedAsString() = if (topSpeed > 0)
        conversionUtil.getSpeed(topSpeed).toInt()
            .toString() + " " + conversionUtil.getSpeedUnit() else "-"

    fun getAverageSpeedAsString() = if (getAverageSpeedInSI() > 0)
        conversionUtil.getSpeed(getAverageSpeedInSI()).toInt()
            .toString() + " " + conversionUtil.getSpeedUnit() else "-"

    fun getTopSpeed() = conversionUtil.getSpeed(topSpeed)

    fun getTopSpeedInMSec() = topSpeed

    fun getDistanceInMetre() = distance

    private fun getAverageSpeedInSI(): Double {
        val timeTaken = TimeUnit.MILLISECONDS.toSeconds((endTime - startTime) - pausedTime)
        return if (timeTaken > 0) (distance.toDouble() / timeTaken.toDouble()) else 0.0
    }

    fun getAverageSpeed() = conversionUtil.getSpeed(getAverageSpeedInSI())

    fun getAverageSpeedInMSec() = getAverageSpeedInSI()

    fun getTotalTime() = endTime - startTime

    companion object {
        fun empty(): DriveReportData {
            return DriveReportData(
                topSpeed = 0.0,
                distance = 0,
                startTime = 0,
                endTime = 0,
                pausedTime = 0
            )
        }
    }
}