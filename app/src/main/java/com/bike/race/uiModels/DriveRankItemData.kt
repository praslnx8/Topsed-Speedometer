package com.bike.race.uiModels

import com.bike.race.utils.ClockUtils
import kotlinx.parcelize.IgnoredOnParcel
import org.koin.core.context.GlobalContext

data class DriveRankItemData(
    val id: Long,
    val tag: String?,
    val topSpeed: Double,
    val avgSpeed: Double,
    val timeTaken: Long,
    val endTime: Long
) {

    @IgnoredOnParcel
    private val clockUtils by lazy {
        GlobalContext.get().get<ClockUtils>()
    }

    fun timeText(): String {
        return clockUtils.getTimeFromMillis(timeTaken)
    }

    fun happenedText(): String {
        return clockUtils.getRelativeTime(endTime)
    }

    fun getTagText(): String {
        return tag?.let {
            "($tag)"
        } ?: ""
    }
}