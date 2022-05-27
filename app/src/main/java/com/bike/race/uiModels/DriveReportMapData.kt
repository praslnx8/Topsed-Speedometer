package com.bike.race.uiModels

import com.bike.race.domain.drive.drivepath.DrivePathItem
import com.bike.race.domain.location.LocationPoint
import com.bike.race.utils.ConversionUtil
import com.bike.race.utils.SphericalUtil
import com.bike.race.utils.toLatLng
import com.google.android.gms.maps.model.*
import org.koin.core.context.GlobalContext

data class DriveReportMapData(
    private val startLocation: LocationPoint?,
    private val endLocation: LocationPoint?,
    private val path: List<DrivePathItem>,
    private val polylineOptionList: List<PolylineOptions>
) {

    private val conversionUtil by lazy {
        GlobalContext.get().get<ConversionUtil>()
    }

    fun getSourcePointMarker(): MarkerOptions? {
        return path.firstOrNull()?.let {
            val locationPoint = it.locationPoint
            MarkerOptions()
                .position(LatLng(locationPoint.latitude, locationPoint.longitude))
                .icon(BitmapDescriptorFactory.defaultMarker(201f))
                .title("Start")
        }
    }

    fun getDestPointMarker(): MarkerOptions? {
        return path.lastOrNull()?.let {
            val locationPoint = it.locationPoint
            MarkerOptions()
                .position(LatLng(locationPoint.latitude, locationPoint.longitude))
                .icon(BitmapDescriptorFactory.defaultMarker(17f))
                .title("End")
        }
    }

    fun getTopSpeedPoint(): MarkerOptions? {
        return path.maxByOrNull {
            it.speed
        }?.let {
            val locationPoint = it.locationPoint
            val topSpeedString = "Top speed ${conversionUtil.getSpeedWithUnit(it.speed.toDouble())}"
            MarkerOptions()
                .position(LatLng(locationPoint.latitude, locationPoint.longitude))
                .icon(BitmapDescriptorFactory.defaultMarker(49f))
                .title(topSpeedString)
        }
    }

    fun getLatLngBounds(): LatLngBounds? {
        if (startLocation != null && endLocation != null) {
            return LatLngBounds.Builder()
                .include(LatLng(startLocation.latitude, startLocation.longitude))
                .include(LatLng(endLocation.latitude, endLocation.longitude)).build()
        }

        return null
    }

    fun getLatLngToZoom(): LatLng? {
        return startLocation?.toLatLng()
    }

    fun getHeading(): Double {
        if (startLocation != null && endLocation != null) {
            return SphericalUtil().computeHeading(
                LatLng(startLocation.latitude, startLocation.longitude),
                LatLng(endLocation.latitude, endLocation.longitude)
            ) - 30
        }

        return 0.0
    }


    fun getMapPolyLineOptionList(): List<PolylineOptions> = polylineOptionList

    companion object {
        fun empty(
            startLocation: LocationPoint? = null,
        ): DriveReportMapData = DriveReportMapData(
            startLocation = startLocation,
            endLocation = null,
            path = emptyList(),
            polylineOptionList = emptyList()
        )
    }
}