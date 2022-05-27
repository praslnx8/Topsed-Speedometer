package com.bike.race.domain.drive.drivepath

import kotlin.math.abs

class SpeedDiffCalculator {

    fun isSpeedDiff(topSpeed: Float, speed: Float, prevSpeed: Float?): Boolean {
        if (speed > topSpeed) {
            return true
        }

        if (prevSpeed == null) {
            return true
        }

        val prevSpeedDiff = (prevSpeed * 100) / topSpeed
        val currentSpeedDiff = (speed * 100) / topSpeed
        return if (prevSpeed > 3 || speed > 3) {
            abs(prevSpeedDiff - currentSpeedDiff) >= 20
        } else {
            false
        }
    }
}