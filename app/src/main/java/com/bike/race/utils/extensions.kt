package com.bike.race.utils

import android.location.Location
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.bike.race.domain.location.LocationPoint
import com.google.android.gms.maps.model.LatLng
import java.util.*

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T) {
            removeObserver(this)
            observer.onChanged(t)
        }
    })
}

fun <T> LiveData<T>.observeForChange(
    lifecycleOwner: LifecycleOwner,
    observer: Observer<T>,
    observeCurrent: Boolean = true,
    isChangedCallback: (prevData: T?, currentData: T) -> Boolean = { a, b -> a != b }
) {
    var isCurrentObtained = false
    observe(lifecycleOwner, object : Observer<T> {
        var prevData: T? = null
        override fun onChanged(t: T) {
            if (isCurrentObtained) {
                if (isChangedCallback(prevData, t)) {
                    observer.onChanged(t)
                }
            } else if (observeCurrent) {
                observer.onChanged(t)
            }
            isCurrentObtained = true
            prevData = t
        }
    })
}

fun LocationPoint.toLatLng(): LatLng {
    return LatLng(latitude, longitude)
}

fun Location.toLocationPoint(): LocationPoint {
    return LocationPoint(latitude, longitude)
}

fun Locale.isMetric(): Boolean {
    return when (country.uppercase()) {
        "US", "LR", "MM" -> false
        else -> true
    }
}