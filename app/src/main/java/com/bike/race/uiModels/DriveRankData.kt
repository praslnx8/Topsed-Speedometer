package com.bike.race.uiModels

data class DriveRankData(
    val startLocality: String,
    val endLocality: String,
    val items: List<DriveRankItemData>,
    val fetching: Boolean,
    val noItems: Boolean
) {
    fun getLocality(): String {
        return "$startLocality - $endLocality"
    }


    companion object {
        fun items(
            startLocality: String,
            endLocality: String,
            items: List<DriveRankItemData>
        ): DriveRankData {
            return DriveRankData(
                startLocality = startLocality,
                endLocality = endLocality,
                items = items,
                fetching = false,
                noItems = false
            )
        }

        fun loading(startLocality: String, endLocality: String): DriveRankData {
            return DriveRankData(
                startLocality = startLocality,
                endLocality = endLocality,
                items = emptyList(),
                fetching = true,
                noItems = false
            )
        }

        fun error(startLocality: String, endLocality: String): DriveRankData {
            return DriveRankData(
                startLocality = startLocality,
                endLocality = endLocality,
                items = emptyList(),
                fetching = false,
                noItems = true
            )
        }
    }
}