package com.bike.race.uiModels

import com.bike.race.utils.ClockUtils
import com.bike.race.utils.ConversionUtil
import com.bike.race.utils.SphericalUtil
import com.google.android.gms.maps.model.*
import org.koin.core.context.GlobalContext
import java.util.concurrent.TimeUnit

data class CompareData(
    val comparableDriveData1: ComparableDriveData,
    val comparableDriveData2: ComparableDriveData?
) {
    fun getLatLngBounds(): LatLngBounds? {

        val start1Loc = comparableDriveData1.getPath().firstOrNull()
        val start2Loc = comparableDriveData2?.getPath()?.firstOrNull()
        val end1Loc = comparableDriveData1.getPath().lastOrNull()
        val end2Loc = comparableDriveData2?.getPath()?.lastOrNull()

        if (start1Loc != null && end1Loc != null) {
            val builder = LatLngBounds.Builder()
            builder.include(LatLng(start1Loc.lat, start1Loc.lon))
            if (start2Loc != null) {
                builder.include(LatLng(start2Loc.lat, start2Loc.lon))
            }
            builder.include(LatLng(end1Loc.lat, end1Loc.lon))
            if (end2Loc != null) {
                builder.include(LatLng(end2Loc.lat, end2Loc.lon))
            }

            return builder.build()
        }

        return null
    }
}

data class ComparableDriveData(
    private val startTime: Long,
    private val endTime: Long,
    private val distance: Int,
    private val topSpeed: Double,
    private val path: List<LocationWithTime>
) {
    private val conversionUtil: ConversionUtil by lazy {
        GlobalContext.get().get()
    }

    private val clockUtils: ClockUtils by lazy {
        GlobalContext.get().get()
    }

    fun getTime(): String {
        return clockUtils.getTimeFromSecs(TimeUnit.MILLISECONDS.toSeconds(endTime - startTime))
    }

    fun getTopSpeed(): String {
        return conversionUtil.getSpeedWithUnit(topSpeed)
    }

    fun getTotalDistance(): String {
        return conversionUtil.getDistanceWithUnit(distance.toDouble())
    }

    fun getAvgSpeed(): String {
        val timeDiff = TimeUnit.MILLISECONDS.toSeconds(endTime - startTime)

        val avgSpeed = if (timeDiff > 0) {
            distance.toDouble() / timeDiff
        } else {
            0.0
        }

        return conversionUtil.getSpeedWithUnit(avgSpeed)
    }

    fun getPolyLine(color: Int): PolylineOptions {
        val polylineOptions = PolylineOptions()
        path.forEach {
            polylineOptions.add(LatLng(it.lat, it.lon))
        }
        polylineOptions.width(12f)
        polylineOptions.color(color)

        return polylineOptions
    }

    fun getStartingMarkerPosition(icon: BitmapDescriptor): MarkerOptions? {
        val point = path.firstOrNull()

        return point?.let {
            MarkerOptions()
                .icon(icon)
                .position(LatLng(point.lat, point.lon))
        }
    }

    fun getNextLocationWithTime(time: Long): LocationWithTime? {
        return path.firstOrNull {
            it.time > time
        } ?: path.lastOrNull()
    }

    fun getStopMarker(): MarkerOptions? {
        return path.lastOrNull()?.let {
            MarkerOptions().position(LatLng(it.lat, it.lon))
                .icon(BitmapDescriptorFactory.defaultMarker(1.0f))
        }
    }

    fun getHeading(): Double {
        val startLocation = path.firstOrNull() ?: return 0.0
        val endLocation = path.lastOrNull() ?: return 0.0
        return SphericalUtil().computeHeading(
            LatLng(startLocation.lat, startLocation.lon),
            LatLng(endLocation.lat, endLocation.lon)
        ) - 30
    }

    fun getPath() = path
}

data class LocationWithTime(
    val lat: Double,
    val lon: Double,
    val time: Long
)