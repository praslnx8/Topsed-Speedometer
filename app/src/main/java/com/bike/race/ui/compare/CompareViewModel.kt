package com.bike.race.ui.compare

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bike.race.domain.drive.drivepath.DrivePathFilter
import com.bike.race.repositories.DriveRepository
import com.bike.race.uiModels.ComparableDriveData
import com.bike.race.uiModels.CompareData
import com.bike.race.uiModels.LocationWithTime
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class CompareViewModel(
    private val driveRepository: DriveRepository,
    private val drivePathFilter: DrivePathFilter
) : ViewModel() {

    private val _compareLiveData = MutableLiveData<CompareData>()
    val compareLiveData: LiveData<CompareData> = _compareLiveData

    fun fetchDataToCompare(
        currentDriveId: Long,
        compareDriveId: Long,
    ) {
        viewModelScope.launch {

            val currentDrive = driveRepository.getDrive(currentDriveId)
            val currentDrivePath =
                drivePathFilter.filter(driveRepository.getDrivePath(currentDriveId) ?: emptyList())

            if (currentDrive == null || currentDrivePath.isEmpty()) {
                return@launch
            }

            val timeScalerValue = when {
                currentDrive.getTotalTime() > TimeUnit.MILLISECONDS.toMinutes(5) -> {
                    TimeUnit.MINUTES.toSeconds(1).toDouble()
                        .div(TimeUnit.MILLISECONDS.toSeconds(currentDrive.getTotalTime()))
                }
                currentDrive.getTotalTime() > TimeUnit.MILLISECONDS.toMinutes(1) -> {
                    30.0.div(TimeUnit.MILLISECONDS.toSeconds(currentDrive.getTotalTime()))
                }
                else -> {
                    1.0
                }
            }

            if (compareDriveId == currentDriveId) {
                val compareData = CompareData(
                    comparableDriveData1 = ComparableDriveData(
                        startTime = currentDrive.startTime,
                        endTime = currentDrive.endTime,
                        distance = currentDrive.distance,
                        topSpeed = currentDrive.topSpeed,
                        path = currentDrivePath.map { p ->
                            LocationWithTime(
                                p.locationPoint.latitude,
                                p.locationPoint.longitude,
                                ((p.time - currentDrive.startTime) * timeScalerValue).toLong()
                            )
                        }
                    ),
                    comparableDriveData2 = null
                )
                _compareLiveData.postValue(compareData)
            } else {

                val compareDrive = driveRepository.getDrive(compareDriveId)
                val compareDrivePath = drivePathFilter.filter(
                    driveRepository.getDrivePath(compareDriveId) ?: emptyList()
                )

                if (compareDrive == null || compareDrivePath.isEmpty()) {
                    return@launch
                }


                val compareData = CompareData(
                    comparableDriveData1 = ComparableDriveData(
                        startTime = currentDrive.startTime,
                        endTime = currentDrive.endTime,
                        distance = currentDrive.distance,
                        topSpeed = currentDrive.topSpeed,
                        path = currentDrivePath.map { p ->
                            LocationWithTime(
                                p.locationPoint.latitude,
                                p.locationPoint.longitude,
                                ((p.time - currentDrive.startTime) * timeScalerValue).toLong()
                            )
                        }
                    ),
                    comparableDriveData2 = ComparableDriveData(
                        startTime = compareDrive.startTime,
                        endTime = compareDrive.endTime,
                        distance = compareDrive.distance,
                        topSpeed = compareDrive.topSpeed,
                        path = compareDrivePath.map { p ->
                            LocationWithTime(
                                p.locationPoint.latitude,
                                p.locationPoint.longitude,
                                ((p.time - compareDrive.startTime) * timeScalerValue).toLong()
                            )
                        }
                    ),
                )
                _compareLiveData.postValue(compareData)
            }
        }
    }
}