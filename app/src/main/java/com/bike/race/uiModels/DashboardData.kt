package com.bike.race.uiModels

import android.os.Parcelable
import com.bike.race.domain.drive.currentDrive.CurrentDriveStatus.PAUSED
import com.bike.race.domain.drive.currentDrive.CurrentDriveStatus.STOPPED
import com.bike.race.utils.ClockUtils
import com.bike.race.utils.ConversionUtil
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import org.koin.core.context.GlobalContext

@Parcelize
data class DashboardData(
    private val currentSpeed: Double,
    private val topSpeed: Double,
    private val averageSpeed: Double,
    private val distance: Int,
    private val runningTime: Long,
    private val status: Int,
    private val gpsSignalStrength: Int
) : Parcelable {

    @IgnoredOnParcel
    private val conversionUtil by lazy {
        GlobalContext.get().get<ConversionUtil>()
    }

    @IgnoredOnParcel
    private val clockUtils by lazy {
        GlobalContext.get().get<ClockUtils>()
    }

    fun getCurrentSpeed() = conversionUtil.getSpeed(currentSpeed).toInt()

    fun getTopSpeed() = conversionUtil.getSpeed(topSpeed).toInt()

    fun getAverageSpeedText() = conversionUtil.getSpeedStr(averageSpeed)

    fun getCurrentSpeedText() = conversionUtil.getSpeedStr(currentSpeed)

    fun getTopSpeedText() = conversionUtil.getSpeedStr(topSpeed)

    fun getDistanceText() = conversionUtil.getDistance(distance.toDouble())

    fun getSpeedUnitText() = conversionUtil.getSpeedUnit()

    fun getDistanceUnitText() = conversionUtil.getDistanceUnit()

    fun getGPSSignalStrength() = gpsSignalStrength

    fun timeText(): String {
        if (runningTime == 0L || runningTime <= 0L) {
            return "00:00"
        }

        return clockUtils.getTimeFromMillis(runningTime)
    }

    fun getStatus(): Int {
        return status
    }

    fun isRunning(): Boolean {
        return status != STOPPED
    }

    fun isPaused(): Boolean {
        return status == PAUSED
    }

    companion object {
        fun empty() = DashboardData(
            runningTime = 0L,
            currentSpeed = 0.0,
            topSpeed = 0.0,
            averageSpeed = 0.0,
            distance = 0,
            status = STOPPED,
            gpsSignalStrength = 0,
        )
    }
}