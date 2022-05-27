package com.bike.race.domain.drive

import com.bike.race.domain.drive.drivepath.SpeedDiffCalculator
import com.google.common.truth.Truth
import org.junit.Test

class SpeedDiffCalculatorTest {


    @Test
    fun `should return true for a top speed change`() {
        Truth.assertThat(SpeedDiffCalculator().isSpeedDiff(20f, 22f, 20f)).isTrue()
    }

    @Test
    fun `should return true for a valid speed change`() {
        Truth.assertThat(SpeedDiffCalculator().isSpeedDiff(20f, 18f, 10f)).isTrue()
        Truth.assertThat(SpeedDiffCalculator().isSpeedDiff(20f, 10f, 2f)).isTrue()
    }

    @Test
    fun `should return false for a minimal speed change`() {
        Truth.assertThat(SpeedDiffCalculator().isSpeedDiff(20f, 12f, 10f)).isFalse()
        Truth.assertThat(SpeedDiffCalculator().isSpeedDiff(20f, 20f, 18f)).isFalse()
    }
}