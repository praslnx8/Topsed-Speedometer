package com.bike.race.uiModels

data class DriveItemCollection(
    val driveItemWithMapList: List<DriveItemWithMapData>,
    val maxFetched: Int
) {
    fun isEmpty() = driveItemWithMapList.isEmpty()
}