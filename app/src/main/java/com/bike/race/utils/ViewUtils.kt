package com.bike.race.utils

import androidx.appcompat.app.AppCompatDelegate
import com.bike.race.domain.preference.UserPreferenceManager


object ViewUtils {

    fun setTheme(themeStyle: UserPreferenceManager.ThemeStyle) {
        when (themeStyle) {
            UserPreferenceManager.ThemeStyle.AUTO -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            )
            UserPreferenceManager.ThemeStyle.LIGHT -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO
            )
            UserPreferenceManager.ThemeStyle.DARK -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES
            )
        }
    }
}