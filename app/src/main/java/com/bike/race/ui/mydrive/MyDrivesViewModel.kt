package com.bike.race.ui.mydrive

import androidx.lifecycle.*
import com.bike.race.domain.drive.MapPolyLineCreator
import com.bike.race.repositories.DriveRepository
import com.bike.race.uiModels.DriveItemCollection
import com.bike.race.uiModels.DriveItemWithMapData
import com.bike.race.utils.RemoteConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyDrivesViewModel(
    private val driveRepository: DriveRepository,
    private val polyLineCreator: MapPolyLineCreator
) : ViewModel() {

    fun getMyDrives(): LiveData<DriveItemCollection> {
        return driveRepository.getDrivesLiveData(
            count = RemoteConfig.getMaximumDrivesToShow()
        ).map { driveList ->

            val driveItemList = driveList.map {
                DriveItemWithMapData(
                    driveId = it.id ?: 0,
                    tag = it.tag,
                    startLocationPoint = it.startLocation,
                    startLocality = it.startLocality,
                    endLocationPoint = it.endLocation,
                    endLocality = it.endLocality,
                    topSpeed = it.topSpeed,
                    distance = it.distance,
                    startTime = it.startTime,
                    endTime = it.endTime,
                    polyLineOptions = polyLineCreator.createLitePolyLineOptions(it.locationPath)
                )
            }

            DriveItemCollection(driveItemList, RemoteConfig.getMaximumDrivesToShow())
        }
    }

    fun getMyDriveStats(): LiveData<DrivesReport> {
        val mediatorLiveData = MediatorLiveData<DrivesReport>()
        var topSpeed = 0.0
        var totalDrives = 0

        mediatorLiveData.addSource(driveRepository.getTopSpeedLiveData()) {
            topSpeed = it ?: 0.0
            mediatorLiveData.value = DrivesReport(topSpeed, totalDrives)
        }

        mediatorLiveData.addSource(driveRepository.getTotalDrives()) {
            totalDrives = it ?: 0
            mediatorLiveData.value = DrivesReport(topSpeed, totalDrives)
        }

        return mediatorLiveData
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