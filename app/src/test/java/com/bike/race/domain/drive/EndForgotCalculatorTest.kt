package com.bike.race.domain.drive

import com.bike.race.domain.drive.currentDrive.EndForgotCalculator
import com.bike.race.domain.location.LocationPoint
import com.bike.race.utils.SphericalUtil
import com.google.common.truth.Truth.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import java.util.concurrent.TimeUnit

class EndForgotCalculatorTest {

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
    fun `should return true if locations are same`() {
        val endForgotCalculator = EndForgotCalculator()

        endForgotCalculator.addPoint(
            TimeUnit.SECONDS.toMillis(1),
            LocationPoint(12.922286, 80.207929)
        )
        endForgotCalculator.addPoint(
            TimeUnit.SECONDS.toMillis(2),
            LocationPoint(12.922277, 80.207929)
        )
        endForgotCalculator.addPoint(
            TimeUnit.SECONDS.toMillis(3),
            LocationPoint(12.922287, 80.207920)
        )

        assertThat(endForgotCalculator.isForgot(1f)).isTrue()
    }

    @Test
    fun `should return false if locations are differrent by small`() {
        val endForgotCalculator = EndForgotCalculator()

        endForgotCalculator.addPoint(
            TimeUnit.SECONDS.toMillis(1),
            LocationPoint(12.928510, 80.201735)
        )
        endForgotCalculator.addPoint(
            TimeUnit.SECONDS.toMillis(2),
            LocationPoint(12.928500, 80.201734)
        )
        endForgotCalculator.addPoint(
            TimeUnit.SECONDS.toMillis(3),
            LocationPoint(12.928509, 80.201737)
        )
        endForgotCalculator.addPoint(
            TimeUnit.SECONDS.toMillis(4),
            LocationPoint(12.928509, 80.201711)
        )

        assertThat(endForgotCalculator.isForgot(1f)).isTrue()
    }
}