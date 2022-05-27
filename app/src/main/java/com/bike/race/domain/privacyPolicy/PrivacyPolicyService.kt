package com.bike.race.domain.privacyPolicy

import com.bike.race.domain.preference.UserPreferenceManager

class PrivacyPolicyService(private val userPreferenceManager: UserPreferenceManager) {

    fun isPolicyAccepted() = userPreferenceManager.getPrivacyPolicyReadStatus()

    fun onAcceptPolicy() = userPreferenceManager.setPrivacyPolicyReadStatus()
}