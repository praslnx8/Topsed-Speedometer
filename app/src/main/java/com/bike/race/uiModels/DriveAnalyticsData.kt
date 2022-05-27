package com.bike.race.uiModels

import android.graphics.Color
import com.bike.race.utils.ClockUtils
import com.bike.race.utils.ConversionUtil
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import org.koin.core.context.GlobalContext
import java.util.*
import java.util.concurrent.TimeUnit

data class DriveAnalyticsData(
    private val driveTag: String?,
    private val startLocality: String?,
    private val endLocality: String?,
    private val noOfStops: Int,
    private val idleTime: Long,
    private val distance: Int,
    private val startTime: Long,
    private val pauseTime: Long,
    private val endTime: Long,
    private val topSpeed: Double,
    private val speedMap: Map<Long, Double>
) {

    private val conversionUtil by lazy {
        GlobalContext.get().get<ConversionUtil>()
    }

    private val clockUtils by lazy {
        GlobalContext.get().get<ClockUtils>()
    }

    fun getStartLocality() = startLocality ?: "-"

    fun getEndLocality() = endLocality ?: "-"

    fun getStartTime() = clockUtils.getTimeFromDate(startTime)

    fun getEndTime() = clockUtils.getTimeFromDate(endTime)

    fun getTotalTime() = (endTime - startTime) - pauseTime

    fun getTopSpeed() =
        if (topSpeed > 0) {
            conversionUtil.getSpeedWithUnit(topSpeed)
        } else {
            "-"
        }

    fun getAverageSpeed(): String {

        val time = TimeUnit.MILLISECONDS.toSeconds(getTotalTime())

        if (time > 0) {
            val avgSpeed = distance.toDouble() / time.toDouble()
            if (avgSpeed > 0) {
                return conversionUtil.getSpeedWithUnit(avgSpeed)
            }
        }

        return "-"
    }

    fun getDistance() = distance

    fun getTotalDistanceAsString() = if (distance > 0)
        conversionUtil.getDistanceWithUnit(distance.toDouble())
    else "-"

    fun getTotalTimeAsString(): String {
        return if (getTotalTime() > 0) {
            clockUtils.getTimeFromSecs(TimeUnit.MILLISECONDS.toSeconds(getTotalTime()))
        } else {
            "-"
        }
    }

    fun getOverallTimeAsString(): String {
        val overallTime = endTime - startTime
        return if (overallTime > 0) {
            clockUtils.getTimeFromSecs(TimeUnit.MILLISECONDS.toSeconds(overallTime))
        } else {
            "-"
        }
    }

    fun getPauseTimeAsString(): String {
        return if (pauseTime > 0) {
            clockUtils.getTimeFromSecs(TimeUnit.MILLISECONDS.toSeconds(pauseTime))
        } else {
            "-"
        }
    }

    fun getNoOfStops() = if (noOfStops > 0) noOfStops.toString() else "-"

    fun getIdleTime() = if (idleTime > 0) clockUtils.getTimeFromMillis(idleTime) else "-"

    fun getLineData(): LineData? {

        if (speedMap.size < 2) {
            return null
        }

        val speedEntries = mutableListOf<Entry>()
        val calendar = Calendar.getInstance()
        var lastEntryTime = 0L

        speedMap.forEach {
            calendar.timeInMillis = it.key
            calendar.set(1970, 0, 1)

            if (TimeUnit.MILLISECONDS.toMinutes(calendar.timeInMillis) > lastEntryTime) {
                lastEntryTime = TimeUnit.MILLISECONDS.toMinutes(calendar.timeInMillis)
                val speed = conversionUtil.getSpeed(it.value)
                speedEntries.add(Entry(calendar.timeInMillis.toFloat(), speed.toFloat()))
            }
        }

        val speedDataSet = LineDataSet(speedEntries, "").apply {
            disableDashedLine()
            setDrawFilled(true)
            setDrawCircles(true)
            circleRadius = 4f
            setDrawValues(false)
            lineWidth = 3f
            color = Color.GREEN
            setCircleColor(Color.GREEN)
        }

        return LineData(speedDataSet)
    }

    fun getTagText(): String {
        return driveTag?.trim() ?: ""
    }

    companion object {
        fun empty(): DriveAnalyticsData = DriveAnalyticsData(
            driveTag = null,
            startLocality = null,
            endLocality = null,
            noOfStops = 0,
            idleTime = 0,
            pauseTime = 0L,
            distance = 0,
            startTime = System.currentTimeMillis(),
            endTime = System.currentTimeMillis(),
            topSpeed = 0.0,
            speedMap = emptyMap()
        )
    }
}