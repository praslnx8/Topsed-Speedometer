package com.bike.race.components

import android.annotation.SuppressLint
import android.location.*
import android.os.Handler
import android.os.Looper
import org.koin.core.context.GlobalContext
import java.util.concurrent.TimeUnit


class LocationProvider : LocationListener, GnssStatus.Callback() {

    private var locationChangeCallback: (Location) -> Unit = {}
    private var gpsSignalCallback: (signalStrength: Int) -> Unit = {}
    private var startTime = System.currentTimeMillis()
    private var currentGPSStrength = 0

    private val locationManager by lazy {
        GlobalContext.get().get<LocationManager>()
    }

    override fun onLocationChanged(location: Location) {
        if (isValidLocation(location)) {
            locationChangeCallback(location)
        }
    }

    @SuppressLint("MissingPermission")
    fun subscribe(
        locationChangeCallback: (Location) -> Unit,
        gpsSignalCallback: (gpsSignalStrength: Int) -> Unit
    ) {
        this.locationChangeCallback = locationChangeCallback
        this.gpsSignalCallback = gpsSignalCallback

        startTime = System.currentTimeMillis()
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0F, this)
        locationManager.registerGnssStatusCallback(
            this,
            Handler(Looper.getMainLooper())
        )
    }

    fun unsubscribe() {
        this.locationChangeCallback = {}
        locationManager.removeUpdates(this)
    }

    override fun onProviderEnabled(provider: String) {
        //Nothing to do
    }

    override fun onProviderDisabled(provider: String) {
        //Nothing to do
    }

    override fun onSatelliteStatusChanged(status: GnssStatus) {
        super.onSatelliteStatusChanged(status)
        status.let { gnsStatus ->
            val totalSatellites = gnsStatus.satelliteCount
            if (totalSatellites > 0) {
                var satellitesFixed = 0
                for (i in 0 until totalSatellites) {
                    if (gnsStatus.usedInFix(i)) {
                        satellitesFixed++
                    }
                }

                currentGPSStrength = (satellitesFixed * 100) / totalSatellites
                gpsSignalCallback(currentGPSStrength)
            }
        }
    }

    private fun isValidLocation(location: Location): Boolean {

        if (location.time < startTime) {
            return false
        }

        if (currentGPSStrength == 0) {
            return false
        }

        if (location.accuracy <= 0 || location.accuracy > 20) {
            return false
        }
        return true
    }
}