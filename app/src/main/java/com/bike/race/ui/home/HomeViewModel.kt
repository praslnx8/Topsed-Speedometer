package com.bike.race.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bike.race.customs.SingleLiveEvent
import com.bike.race.domain.drive.DriveLocalityAddService
import com.bike.race.uiModels.DashboardData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(
    private val driveLocalityAddService: DriveLocalityAddService,
) : ViewModel() {

    private val _dashboardLiveData = MutableLiveData<DashboardData>().apply {
        value = DashboardData.empty()
    }

    val dashboardLiveData: LiveData<DashboardData> = _dashboardLiveData

    private val _newTopSpeedLiveData = MutableLiveData<String>()
    val newTopSpeedLiveData: LiveData<String> = _newTopSpeedLiveData

    val startStopLiveData = SingleLiveEvent<Int>().apply {
        value = DriveAction.STOP
    }

    fun updateDashboardData(dashboardData: DashboardData) {
        checkAndTriggerTopSpeed(dashboardData)
        _dashboardLiveData.value = dashboardData
    }

    fun syncServices() {
        viewModelScope.launch(Dispatchers.IO) {
            driveLocalityAddService.addLocalityInformation()
        }
    }

    fun startDrive() {
        startStopLiveData.value = DriveAction.START
    }

    fun pauseDrive() {
        startStopLiveData.value = DriveAction.PAUSE
    }

    fun stopDrive() {
        startStopLiveData.value = DriveAction.STOP
    }

    private fun checkAndTriggerTopSpeed(dashboardData: DashboardData) {

        val prevDashboardData = _dashboardLiveData.value ?: return
        if (dashboardData.getTopSpeed() > prevDashboardData.getTopSpeed()) {
            _newTopSpeedLiveData.value = dashboardData.getTopSpeedText()
        }
    }
}