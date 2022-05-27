package com.bike.race.ui.driveReport

import androidx.lifecycle.*
import com.bike.race.domain.MapType
import com.bike.race.domain.drive.DriveLocalityAddService
import com.bike.race.domain.drive.DriveStatAnalyser
import com.bike.race.domain.drive.MapPolyLineCreator
import com.bike.race.domain.drive.drivepath.DrivePathFilter
import com.bike.race.domain.drive.drivepath.DrivePathItem
import com.bike.race.domain.location.LocationPoint
import com.bike.race.domain.preference.UserPreferenceManager
import com.bike.race.repositories.DriveRepository
import com.bike.race.uiModels.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class DriveReportViewModel(
    private val driveRepository: DriveRepository,
    private val mapPolyLineCreator: MapPolyLineCreator,
    private val driveStatAnalyser: DriveStatAnalyser,
    private val driveLocalityAddService: DriveLocalityAddService,
    private val userPreferenceManager: UserPreferenceManager,
    private val drivePathFilter: DrivePathFilter
) : ViewModel() {

    private var driveId: Long = -1L

    fun setInitData(driveId: Long) {
        this.driveId = driveId
    }

    fun getCurrentDriveId(): Long {
        return this.driveId
    }

    fun getDriveReportLiveData(): LiveData<DriveReportData> {
        return driveRepository.getDriveLiveData(driveId).switchMap { drive ->

            liveData(context = viewModelScope.coroutineContext + Dispatchers.Default) {

                val driveReportData = drive?.let {
                    DriveReportData(
                        topSpeed = drive.topSpeed,
                        distance = drive.distance,
                        startTime = drive.startTime,
                        endTime = drive.endTime,
                        pausedTime = drive.pauseTime
                    )
                } ?: DriveReportData.empty()

                emit(driveReportData)
            }
        }
    }

    fun getDriveReportMapLiveData(defaultLocation: LocationPoint?): LiveData<DriveReportMapData> {
        return driveRepository.getDriveLiveData(driveId).switchMap { drive ->

            liveData(context = viewModelScope.coroutineContext + Dispatchers.Default) {

                val driveReportMapData = drive?.let {
                    val drivePath: List<DrivePathItem> =
                        drivePathFilter.filter(driveRepository.getDrivePath(driveId) ?: emptyList())
                    DriveReportMapData(
                        startLocation = drive.startLocation,
                        endLocation = drive.endLocation,
                        path = drivePath,
                        polylineOptionList = mapPolyLineCreator.createPolyLineOptions(
                            drivePath,
                            drive.topSpeed
                        )
                    )
                } ?: DriveReportMapData.empty(
                    startLocation = defaultLocation
                )

                emit(driveReportMapData)
            }
        }
    }

    fun getDriveAnalyticsLiveData(): LiveData<DriveAnalyticsData> {
        return driveRepository.getDriveLiveData(driveId).switchMap { drive ->

            liveData(context = viewModelScope.coroutineContext + Dispatchers.Default) {

                val driveAnalyticsData = drive?.let {
                    val drivePath: List<DrivePathItem> =
                        drivePathFilter.filter(driveRepository.getDrivePath(driveId) ?: emptyList())

                    val driveStatsData =
                        driveStatAnalyser.getDriveStatsData(drivePath)

                    val speedMap = driveStatAnalyser.getDriveSpeedMap(drivePath)

                    DriveAnalyticsData(
                        driveTag = drive.tag,
                        startLocality = drive.startLocality,
                        endLocality = drive.endLocality,
                        noOfStops = driveStatsData.noOfStops,
                        idleTime = driveStatsData.idleTime,
                        pauseTime = drive.pauseTime,
                        distance = drive.distance,
                        startTime = drive.startTime,
                        endTime = drive.endTime,
                        topSpeed = drive.topSpeed,
                        speedMap = speedMap
                    )
                } ?: DriveAnalyticsData.empty()

                emit(driveAnalyticsData)
            }
        }
    }

    fun getDriveRankLiveData(): LiveData<DriveRankData> {
        return driveRepository.getDriveLiveData(driveId).switchMap { drive ->

            liveData(context = viewModelScope.coroutineContext + Dispatchers.Default) {
                if (drive == null || drive.startLocality.isNullOrBlank() || drive.endLocality.isNullOrBlank() || drive.distance < 800) {
                    emit(DriveRankData.error(drive?.startLocality ?: "", drive?.endLocality ?: ""))
                    return@liveData
                }

                emit(DriveRankData.loading(drive.startLocality ?: "", drive.endLocality ?: ""))

                val distanceBuffer = (drive.distance * 50 / 100)
                val driveRankItemDatas = driveRepository.getDrivesForRank(
                    startLocality = drive.startLocality ?: "",
                    endLocality = drive.endLocality ?: "",
                    distanceHigherLimit = drive.distance + distanceBuffer,
                    distanceLowerLimit = drive.distance - distanceBuffer
                ).map {
                    DriveRankItemData(
                        id = it.id ?: 0,
                        tag = it.tag,
                        topSpeed = it.topSpeed,
                        avgSpeed = it.getAvgSpeed(),
                        endTime = it.endTime,
                        timeTaken = it.getTotalTime()
                    )
                }
                emit(
                    DriveRankData.items(
                        drive.startLocality ?: "",
                        drive.endLocality ?: "",
                        driveRankItemDatas
                    )
                )
            }
        }
    }

    fun checkAndUpdateDrive() {
        viewModelScope.launch(Dispatchers.IO) {
            driveRepository.getDrive(driveId)?.let { drive ->
                if (drive.startLocality == null || drive.endLocality == null) {
                    driveLocalityAddService.getAndUpdateLocality(drive)
                }
            }
        }
    }

    fun getMapType(): MapType {
        return userPreferenceManager.getPreferredMapType()
    }

    fun toggleMapType(): MapType {
        val currentMapType = userPreferenceManager.getPreferredMapType()
        return if (currentMapType == MapType.NORMAL) {
            MapType.SATELLITE.also {
                userPreferenceManager.setPreferredMapType(it)
            }
        } else {
            MapType.NORMAL.also {
                userPreferenceManager.setPreferredMapType(it)
            }
        }
    }

    fun changeTag(tag: String) {
        viewModelScope.launch {
            driveRepository.updateTag(driveId, tag)
        }
    }
}