package com.bike.race.domain.drive

import android.content.Context
import androidx.core.content.ContextCompat
import com.bike.race.R
import com.bike.race.components.threadPool.Parallel
import com.bike.race.domain.drive.drivepath.DrivePathItem
import com.bike.race.domain.location.LocationPoint
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.context.GlobalContext


class MapPolyLineCreator {

    private val context: Context by lazy {
        GlobalContext.get().get()
    }

    fun createLitePolyLineOptions(path: List<LocationPoint>): PolylineOptions {

        val polylineOptions = PolylineOptions()
        path.forEach { pathItem ->
            polylineOptions.add(
                LatLng(
                    pathItem.latitude,
                    pathItem.longitude
                )
            )
        }

        polylineOptions.geodesic(false)
        polylineOptions.width(12f)
        return polylineOptions.color(ContextCompat.getColor(context, R.color.mapPolylineBlue))

    }

    suspend fun createPolyLineOptions(
        path: List<DrivePathItem>,
        topSpeed: Double
    ) = withContext(Dispatchers.Default) {
        val polylineOptionList = mutableListOf<PolylineOptions>()

        Parallel.forEach(path) {
            val polylineOptions = PolylineOptions()
            polylineOptions.width(15f)
            polylineOptions.startCap(RoundCap())
            polylineOptions.endCap(RoundCap())
            polylineOptions.add(LatLng(it.locationPoint.latitude, it.locationPoint.longitude))
            if (it.nextLocationPoint != null) {
                polylineOptions.add(
                    LatLng(
                        it.nextLocationPoint.latitude,
                        it.nextLocationPoint.longitude
                    )
                )
            }

            polylineOptions.geodesic(false)
            polylineOptions.color(getColorBasedOnSpeed(topSpeed, it.speed.toDouble()))
            polylineOptionList.add(polylineOptions)
        }

        polylineOptionList
    }

    private fun getColorBasedOnSpeed(
        topSpeed: Double,
        speedInMetrePerSecond: Double
    ): Int {
        val percentage = if (topSpeed > 0) (speedInMetrePerSecond / topSpeed) * 100 else 100.0
        return when {
            percentage < 33 -> {
                ContextCompat.getColor(context, R.color.polylineSpeedLow)
            }
            percentage < 66 -> {
                ContextCompat.getColor(context, R.color.polylineSpeedMedium)
            }
            else -> {
                ContextCompat.getColor(context, R.color.polylineSpeedHigh)
            }
        }
    }
}