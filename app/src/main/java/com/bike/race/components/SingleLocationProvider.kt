package com.bike.race.components

import android.annotation.SuppressLint
import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import com.bike.race.utils.LocationPermissionUtils


class SingleLocationProvider(private val locationManager: LocationManager) {

    @SuppressLint("MissingPermission")
    fun getLocation(context: Context): Location? {

        if (LocationPermissionUtils.isBasicPermissionGranted(context) &&
            LocationPermissionUtils.isLocationEnabled(context)
        ) {

            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_COARSE
            criteria.isSpeedRequired = false
            criteria.isCostAllowed = true
            criteria.isBearingRequired = false
            criteria.isAltitudeRequired = false
            criteria.powerRequirement = Criteria.POWER_MEDIUM
            criteria.horizontalAccuracy = Criteria.ACCURACY_LOW
            criteria.verticalAccuracy = Criteria.ACCURACY_LOW

            return locationManager.getBestProvider(criteria, true)?.let {
                locationManager.getLastKnownLocation(it)
            }
        }

        return null
    }
}