package com.bike.race.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.bike.race.db.dbModels.DriveEntity
import com.bike.race.db.dbModels.DrivePathItemEntity

@Dao
interface DriveDao {

    //--------------DRIVE TABLE---------------

    @Query("SELECT * FROM DRIVE ORDER BY CREATED_AT DESC LIMIT :count")
    fun getDrivesLiveData(count: Int): LiveData<List<DriveEntity>>

    @Query("SELECT MAX(TOP_SPEED) FROM DRIVE")
    fun getTopSpeed(): LiveData<Double>

    @Query("SELECT COUNT(ID) FROM DRIVE")
    fun getTotalDrives(): LiveData<Int>

    @Query("SELECT * FROM DRIVE ORDER BY CREATED_AT DESC LIMIT :count OFFSET :offset")
    fun getDrivesLiveData(offset: Int, count: Int): LiveData<List<DriveEntity>>

    @Query("SELECT * FROM DRIVE WHERE START_LOCALITY IS null OR END_LOCALITY IS null ORDER BY CREATED_AT DESC LIMIT :count OFFSET :offset")
    suspend fun getDrivesWithNoLocalityInfo(offset: Int, count: Int): List<DriveEntity>

    @Query("SELECT * FROM DRIVE WHERE ID = :id")
    fun getDriveLiveData(id: Long): LiveData<DriveEntity?>

    @Query("SELECT * FROM DRIVE WHERE ID = :id")
    suspend fun getDrive(id: Long): DriveEntity?

    @Query("SELECT * FROM DRIVE WHERE START_LOCALITY = :startLocality AND END_LOCALITY = :endLocality AND DISTANCE <= :distanceHigherLimit AND DISTANCE >= :distanceLowerLimit ORDER BY ABS(END_TIME - START_TIME) ASC LIMIT 10")
    suspend fun getDrives(
        startLocality: String,
        endLocality: String,
        distanceHigherLimit: Int,
        distanceLowerLimit: Int
    ): List<DriveEntity>

    @Insert
    suspend fun addDrive(driveEntity: DriveEntity): Long

    @Query("UPDATE DRIVE SET START_LOCALITY = :startLocality, END_LOCALITY = :endLocality WHERE id = :driveId")
    suspend fun updateLocalityInfo(startLocality: String, endLocality: String, driveId: Long)

    @Query("UPDATE DRIVE SET TAG = :tag WHERE id = :id")
    suspend fun updateTag(id: Long, tag: String?)

    @Query("DELETE FROM DRIVE WHERE id = :id")
    suspend fun deleteDrive(id: Long)

    //--------------DRIVE_PATH TABLE----------------------------------------------------------------------
    @Query("SELECT * FROM DRIVE_PATH_ITEM WHERE DRIVE_ID = :id ORDER BY sequence ASC LIMIT 500")
    suspend fun getDrivePathItems(id: Long): List<DrivePathItemEntity>?

    @Insert
    suspend fun addDrivePathItems(drivePathItems: List<DrivePathItemEntity>)
}