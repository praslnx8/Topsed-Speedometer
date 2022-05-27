package com.bike.race.domain.location

import android.content.Context
import android.location.Address
import android.location.Geocoder
import java.util.*

class LocalityInfoCollector(
    private val context: Context,
) {

    fun getLocalityInfo(lat: Double, lon: Double): String? {
        return getLocationInfo(lat, lon)?.locality
    }

    private fun getLocationInfo(lat: Double, lon: Double): LocationInfo? {

        if (!Geocoder.isPresent()) {
            return null
        }

        val gcd = Geocoder(context, Locale.getDefault())

        return try {
            val addressList = gcd.getFromLocation(lat, lon, 1)
            if (addressList != null && addressList.isNotEmpty()) {
                parseLocationInfoFromAddressList(addressList)
            } else {
                null
            }
        } catch (ignore: Exception) {
            null
        }
    }

    private fun parseLocationInfoFromAddressList(addressList: List<Address>): LocationInfo? {
        var fetchedLocality: String? = null
        var fetchedCity: String? = null
        var fetchedCountry: String? = null
        var fetchedCountryCode: String? = null

        for (addressData in addressList) {
            val street = addressData.getAddressLine(0)
            val locality = addressData.locality
            val subLocality = addressData.subLocality
            fetchedCity = addressData.adminArea
            fetchedCountry = addressData.countryName
            fetchedCountryCode = addressData.countryCode

            if ((locality != null && locality.trim()
                    .lowercase() != "null" && locality.trim()
                    .isNotBlank()) || (subLocality != null && subLocality.trim()
                    .lowercase() != "null" && subLocality.trim().isNotBlank())
            ) {

                if (locality != null && locality.trim()
                        .isNotBlank() && subLocality != null && subLocality.trim().isNotBlank()
                ) {
                    fetchedLocality = "${subLocality.trim()}, ${locality.trim()}"
                    break
                } else if (locality != null && locality.trim().isNotBlank()) {
                    fetchedLocality = locality.trim()
                    break
                } else if (subLocality != null && subLocality.trim().isNotBlank()) {
                    fetchedLocality = subLocality.trim()
                    break
                }
            } else if (street != null && street.trim().isNotBlank() && street.trim()
                    .lowercase() != "null"
            ) {
                fetchedLocality = street
            }
        }

        return if (fetchedLocality != null && fetchedCountry != null && fetchedCountryCode != null) {
            LocationInfo(fetchedLocality, fetchedCity, fetchedCountry, fetchedCountryCode)
        } else {
            null
        }
    }

}