package com.bike.race.utils

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ClockUtilsTest {


    @Test
    fun `should return time text from seconds`() {
        val clockUtils = ClockUtils()

        assertThat(clockUtils.getTimeFromSecs(8820)).isEqualTo("02:27")
        assertThat(clockUtils.getTimeFromSecs(2727)).isEqualTo("45:27")
    }

    @Test
    fun `should return time text from date`() {
        val clockUtils = ClockUtils()

        assertThat(clockUtils.getTimeFromDate(1600701444000)).isEqualTo("03:17")
    }
}