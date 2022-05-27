package com.bike.race.uiModels

import com.bike.race.domain.location.LocationPoint
import com.bike.race.utils.ClockUtils
import com.bike.race.utils.ConversionUtil
import org.koin.core.context.GlobalContext
import java.util.concurrent.TimeUnit

data class DriveItemData(
    private val driveId: Long,
    private val tag: String?,
    private val startLocationPoint: LocationPoint,
    private val startLocality: String?,
    private val endLocationPoint: LocationPoint,
    private val endLocality: String?,
    private val topSpeed: Double,
    private val distance: Int,
    private val startTime: Long,
    private val endTime: Long
) {

    private val conversionUtil by lazy {
        GlobalContext.get().get<ConversionUtil>()
    }

    private val clockUtils by lazy {
        GlobalContext.get().get<ClockUtils>()
    }


    fun getTagText(): String {
        return tag ?: ""
    }

    fun getTagOrSourceDestText(): String {
        return tag ?: "${startLocality ?: ""} - ${endLocality ?: ""}"
    }

    fun getTopSpeed() = conversionUtil.getSpeedWithUnit(topSpeed)

    fun getTotalTime() =
        clockUtils.getTimeFromSecs(TimeUnit.MILLISECONDS.toSeconds(endTime - startTime))

    fun getTimeText() = clockUtils.getRelativeTime(endTime)

    fun getDriveId() = driveId

    fun getTotalDistance() = conversionUtil.getDistanceWithUnit(distance.toDouble())

}