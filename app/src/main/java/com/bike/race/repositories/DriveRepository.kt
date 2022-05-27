package com.bike.race.repositories

import androidx.lifecycle.map
import androidx.room.Transaction
import com.bike.race.db.AppDatabase
import com.bike.race.db.dao.DriveDao
import com.bike.race.db.dbModels.DriveEntity
import com.bike.race.db.dbModels.DrivePathItemEntity
import com.bike.race.domain.drive.Drive
import com.bike.race.domain.drive.drivepath.DrivePathItem
import com.bike.race.domain.location.LocationPoint

class DriveRepository(private val appDatabase: AppDatabase) {

    private val driveDao: DriveDao by lazy {
        appDatabase.driveDao()
    }

    @Transaction
    suspend fun addDrive(drive: Drive, drivePath: List<DrivePathItem>): Long {
        val driveId = driveDao.addDrive(drive.toDriveEntity())

        driveDao.addDrivePathItems(drivePath.mapIndexed { index, drivePathItem ->
            drivePathItem.toDrivePathItemEntity(driveId, index)
        })

        return driveId
    }

    fun getDrivesLiveData(count: Int = 3) =
        appDatabase.driveDao().getDrivesLiveData(count).map {
            it.map { driveEntity ->
                driveEntity.toDrive()
            }
        }

    fun getTopSpeedLiveData() = appDatabase.driveDao().getTopSpeed()

    fun getTotalDrives() = appDatabase.driveDao().getTotalDrives()

    fun getDrivesLiveData(offset: Int = 0, count: Int = 10) =
        appDatabase.driveDao().getDrivesLiveData(offset, count).map {
            it.map { driveEntity ->
                driveEntity.toDrive()
            }

        }

    suspend fun getDrivesWithNoLocalityInfo(offset: Int = 0, count: Int = 5) =
        appDatabase.driveDao().getDrivesWithNoLocalityInfo(offset, count).map {
            it.toDrive()
        }

    fun getDriveLiveData(id: Long) = appDatabase.driveDao().getDriveLiveData(id).map {
        it?.toDrive()
    }

    suspend fun getDrive(id: Long) = appDatabase.driveDao().getDrive(id)?.toDrive()

    suspend fun getDrivesForRank(
        startLocality: String,
        endLocality: String,
        distanceHigherLimit: Int,
        distanceLowerLimit: Int
    ) = appDatabase.driveDao()
        .getDrives(startLocality, endLocality, distanceHigherLimit, distanceLowerLimit).map {
            it.toDrive()
        }

    suspend fun getDrivePath(driveId: Long): List<DrivePathItemEntity>? {
        return try {
            return driveDao.getDrivePathItems(driveId)
        } catch (ignore: Exception) {
            emptyList()
        }
    }

    suspend fun updateDriveLocality(driveId: Long, startLocality: String, endLocality: String) {
        appDatabase.driveDao().updateLocalityInfo(startLocality, endLocality, driveId)
    }

    suspend fun updateTag(raceId: Long, tag: String?) {
        appDatabase.driveDao().updateTag(raceId, tag)
    }

    suspend fun deleteRace(raceId: Long) {
        appDatabase.driveDao().deleteDrive(raceId)
    }


    private fun Drive.toDriveEntity() = DriveEntity(
        id = id,
        tag = tag,
        startLocationPoint = startLocation,
        startLocality = startLocality,
        endLocationPoint = endLocation,
        endLocality = endLocality,
        startTime = startTime,
        pauseTime = pauseTime,
        endTime = endTime,
        topSpeed = topSpeed,
        distance = distance,
        locationPath = locationPath,
        createdAt = createdAt
    )

    private fun DriveEntity.toDrive() = Drive(
        id = id,
        tag = tag,
        startLocation = startLocationPoint,
        startLocality = startLocality,
        endLocation = endLocationPoint,
        endLocality = endLocality,
        startTime = startTime,
        pauseTime = pauseTime,
        endTime = endTime,
        topSpeed = topSpeed,
        distance = distance,
        locationPath = locationPath,
        createdAt = createdAt
    )

    private fun DrivePathItem.toDrivePathItemEntity(driveId: Long, sequenceId: Int) =
        DrivePathItemEntity(
            id = 0,
            driveId = driveId,
            lat = locationPoint.latitude,
            lon = locationPoint.longitude,
            speed = speed,
            time = time,
            duration = duration,
            distance = distance,
            sequence = sequenceId
        )
}