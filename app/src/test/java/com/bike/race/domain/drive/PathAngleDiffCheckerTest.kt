package com.bike.race.domain.drive

import com.bike.race.domain.drive.drivepath.PathAngleDiffChecker
import com.bike.race.domain.location.LocationPoint
import com.bike.race.utils.SphericalUtil
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest

class PathAngleDiffCheckerTest : KoinTest {

    @Before
    fun setup() {
        startKoin {
            koin.loadModules(listOf(module {
                single { SphericalUtil() }
            }))
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `should return true for valid angle check`() {
        val pathAngleDiffChecker = PathAngleDiffChecker()
        pathAngleDiffChecker.addLocationPoint(LocationPoint(12.923884, 80.197503), 1f, 1L)
        pathAngleDiffChecker.addLocationPoint(LocationPoint(12.926007, 80.197541), 1f, 1L)

        assertThat(
            pathAngleDiffChecker.isAngleDiff(
                LocationPoint(12.928031, 80.199006),
                true
            )
        ).isTrue()
    }

    @Test
    fun `should return false for straight lines`() {
        val pathAngleDiffChecker = PathAngleDiffChecker()
        pathAngleDiffChecker.addLocationPoint(LocationPoint(12.935291, 80.204155), 1f, 1L)
        pathAngleDiffChecker.addLocationPoint(LocationPoint(12.937395, 80.204823), 1f, 1L)

        assertThat(
            pathAngleDiffChecker.isAngleDiff(
                LocationPoint(12.939257, 80.205339),
                true
            )
        ).isFalse()
    }
}