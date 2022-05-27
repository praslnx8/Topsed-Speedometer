package com.bike.race.ui.myAllDrive

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.bike.race.repositories.DriveRepository
import com.bike.race.uiModels.DriveItemData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyAllDrivesViewModel(
    private val driveRepository: DriveRepository
) : ViewModel() {

    @Synchronized
    fun getMyDrivesLiveData(): LiveData<List<DriveItemData>> {
        return driveRepository.getDrivesLiveData(offset = 0, count = 50).map { driveList ->
            driveList.map {
                DriveItemData(
                    driveId = it.id ?: 0,
                    tag = it.tag,
                    startLocationPoint = it.startLocation,
                    startLocality = it.startLocality,
                    endLocationPoint = it.endLocation,
                    endLocality = it.endLocality,
                    topSpeed = it.topSpeed,
                    distance = it.distance,
                    startTime = it.startTime,
                    endTime = it.endTime
                )
            }
        }

    }

    fun deleteDrive(driveId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            driveRepository.deleteRace(driveId)
        }
    }

    fun changeTag(driveId: Long, tag: String) {
        viewModelScope.launch(Dispatchers.IO) {
            driveRepository.updateTag(driveId, tag)
        }
    }
}