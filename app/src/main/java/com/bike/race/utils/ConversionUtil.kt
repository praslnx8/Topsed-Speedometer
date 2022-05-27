package com.bike.race.utils

import android.icu.text.DecimalFormat
import android.icu.text.MeasureFormat
import android.icu.util.Measure
import android.icu.util.MeasureUnit
import com.bike.race.domain.preference.UserPreferenceManager
import org.koin.core.context.GlobalContext
import java.util.*

/**
 * Created by prasi on 27/11/16.
 */

private const val KM_MULT = 3.6
private const val MILES_MULT = 2.23694
private const val SECONDS_HOUR_MULT = 0.000277778
private const val METRE_KM = 0.001
private const val METRE_MILES = 0.000621371

class ConversionUtil {

    private val userPreferenceManager by lazy {
        GlobalContext.get().get<UserPreferenceManager>()
    }

    private val decimalFormatter = MeasureFormat.getInstance(
        Locale.getDefault(),
        MeasureFormat.FormatWidth.SHORT,
        DecimalFormat("#.#")
    )
    private val normalFormatter = MeasureFormat.getInstance(
        Locale.getDefault(),
        MeasureFormat.FormatWidth.SHORT,
        DecimalFormat("#")
    )

    private val isImperial
        get() = userPreferenceManager.getDistanceUnit() == UserPreferenceManager.DistanceUnit.MILES

    fun getDistanceUnit(): String = if (isImperial) {
        decimalFormatter.format(Measure(0, MeasureUnit.MILE)).replace("0", "").trim()
    } else {
        decimalFormatter.format(Measure(0, MeasureUnit.KILOMETER)).replace("0", "").trim()
    }

    fun getSpeedUnit(): String = if (isImperial) {
        normalFormatter.format(Measure(0, MeasureUnit.MILE_PER_HOUR)).replace("0", "").trim()
    } else {
        normalFormatter.format(Measure(0, MeasureUnit.KILOMETER_PER_HOUR)).replace("0", "").trim()
    }

    fun getDistance(metre: Double): String = if (isImperial) {
        roundDouble(convertToMiles(metre)).toString()
    } else {
        roundDouble(convertToKm(metre)).toString()
    }

    fun getSpeed(metrePerSecond: Double): Double = if (isImperial) {
        convertToMilesPerHr(metrePerSecond).toInt().toDouble()
    } else {
        convertToKmPerHr(metrePerSecond).toInt().toDouble()
    }

    fun getDistanceWithUnit(metre: Double): String = if (isImperial) {
        decimalFormatter.format(Measure(convertToMiles(metre), MeasureUnit.MILE))
    } else {
        decimalFormatter.format(Measure(convertToKm(metre), MeasureUnit.KILOMETER))
    }

    fun getSpeedWithUnit(metrePerSecond: Double): String = if (isImperial) {
        normalFormatter.format(
            Measure(
                convertToMilesPerHr(metrePerSecond),
                MeasureUnit.MILE_PER_HOUR
            )
        )
    } else {
        normalFormatter.format(
            Measure(
                convertToKmPerHr(metrePerSecond),
                MeasureUnit.KILOMETER_PER_HOUR
            )
        )
    }

    fun getSpeedStr(
        metrePerSecond: Double
    ): String {
        val speed: Double = getSpeed(metrePerSecond)
        return speed.toInt().toString()
    }

    fun convertToHour(sec: Double): Double {
        return if (sec > 0.2) {
            sec * SECONDS_HOUR_MULT
        } else 0.0
    }

    private fun convertToKm(metre: Double): Double {
        return if (metre > 10) {
            metre * METRE_KM
        } else 0.0
    }

    private fun convertToMiles(metre: Double): Double {
        return if (metre > 20) {
            metre * METRE_MILES
        } else 0.0
    }

    fun convertToKmPerHr(metrePerSec: Double): Double {
        return if (metrePerSec > 0.3) {
            metrePerSec * KM_MULT
        } else 0.0
    }

    fun convertToMetrePerSec(kmPerHour: Double): Double {
        return kmPerHour / KM_MULT
    }

    private fun convertToMilesPerHr(metrePerSec: Double): Double {
        return if (metrePerSec > 0.5) {
            metrePerSec * MILES_MULT
        } else 0.0
    }

    private fun roundDouble(value: Double) = DecimalFormat("#.#").format(value)

}