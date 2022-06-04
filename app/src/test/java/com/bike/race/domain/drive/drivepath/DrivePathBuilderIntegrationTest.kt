package com.bike.race.domain.drive.drivepath

import com.bike.race.utils.SphericalUtil
import com.google.common.truth.Truth
import org.junit.Rule
import org.junit.Test
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import kotlin.test.Ignore

class DrivePathBuilderIntegrationTest : KoinTest {

    private val helloModule = module {
        single { PathAngleDiffChecker() }
        single { SphericalUtil() }
        single { SpeedDiffCalculator() }
    }

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        printLogger()
        modules(helloModule)
    }

    @Ignore("Problem with Koin")
    @Test
    fun `unit test`() {
        val drivePathBuilder = DrivePathBuilder()

        Truth.assertThat(drivePathBuilder.getRacePath()).isEmpty()
    }
}