package com.bike.race.domain.location

data class LocationInfo(
    val locality: String,
    val city: String?,
    val country: String,
    val countryCode: String
)