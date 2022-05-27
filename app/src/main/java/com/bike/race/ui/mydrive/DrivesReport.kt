package com.bike.race.ui.mydrive

import android.os.Parcelable
import com.bike.race.utils.ConversionUtil
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import org.koin.core.context.GlobalContext

@Parcelize
data class DrivesReport(
    private val topSpeed: Double,
    private val totalDrives: Int
) : Parcelable {

    @IgnoredOnParcel
    private val conversionUtil by lazy {
        GlobalContext.get().get<ConversionUtil>()
    }

    fun getTopSpeed(): String {
        return conversionUtil.getSpeedWithUnit(topSpeed)
    }

    fun getTotalDrives(): String {
        return totalDrives.toString()
    }
}