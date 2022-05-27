package com.bike.race.ui.splash

import androidx.lifecycle.ViewModel
import com.bike.race.domain.privacyPolicy.PrivacyPolicyService


class SplashViewModel(
    private val privacyPolicyService: PrivacyPolicyService,
) : ViewModel() {

    fun isPrivacyPolicyAccepted(): Boolean {
        return privacyPolicyService.isPolicyAccepted()
    }

    fun acceptPrivacyPolicy() {
        privacyPolicyService.onAcceptPolicy()
    }
}