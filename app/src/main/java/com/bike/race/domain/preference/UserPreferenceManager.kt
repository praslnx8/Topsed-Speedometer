package com.bike.race.domain.preference

import android.content.Context
import androidx.preference.PreferenceManager
import com.bike.race.domain.MapType
import com.bike.race.utils.EnumCaster
import com.bike.race.utils.isMetric
import java.util.*

class UserPreferenceManager(private val context: Context) {

    fun getPrivacyPolicyReadStatus(): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean("privacy_read_status", false)
    }

    fun setPrivacyPolicyReadStatus() {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
            .putBoolean("privacy_read_status", true).apply()
    }

    init {
        loadDefault()
    }

    private fun loadDefault() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        when (getDistanceUnit()) {
            DistanceUnit.KILOMETERS -> sharedPreferences.edit().putString("distance_unit", "km")
                .apply()
            DistanceUnit.MILES -> sharedPreferences.edit().putString("distance_unit", "miles")
                .apply()
        }
    }

    fun getDistanceUnit(): DistanceUnit {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val distanceUnit = sharedPreferences.getString("distance_unit", null)
        return when {
            distanceUnit == "km" -> {
                DistanceUnit.KILOMETERS
            }
            distanceUnit == "miles" -> {
                DistanceUnit.MILES
            }
            Locale.getDefault().isMetric() -> {
                DistanceUnit.KILOMETERS
            }
            else -> {
                DistanceUnit.MILES
            }
        }
    }

    fun getPreferredMapType(): MapType {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val mapType = sharedPreferences.getInt("map_type", MapType.NORMAL.value)

        return EnumCaster.fromInt<MapType>(mapType) ?: MapType.NORMAL
    }

    fun setPreferredMapType(mapType: MapType) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        sharedPreferences.edit().putInt("map_type", mapType.value).apply()
    }

    fun getTheme(): ThemeStyle {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return when (sharedPreferences.getString("theme", "light")) {
            "light" -> {
                ThemeStyle.LIGHT
            }
            "dark" -> {
                ThemeStyle.DARK
            }
            else -> {
                ThemeStyle.AUTO
            }
        }
    }

    enum class DistanceUnit {
        KILOMETERS,
        MILES
    }

    enum class ThemeStyle {
        AUTO,
        LIGHT,
        DARK
    }
}