package com.bike.race.di

import android.content.Context
import android.location.LocationManager
import com.bike.race.components.LocationProvider
import com.bike.race.components.SingleLocationProvider
import com.bike.race.db.AppDatabase
import com.bike.race.domain.drive.DriveLocalityAddService
import com.bike.race.domain.drive.DriveService
import com.bike.race.domain.drive.DriveStatAnalyser
import com.bike.race.domain.drive.MapPolyLineCreator
import com.bike.race.domain.drive.currentDrive.EndForgotCalculator
import com.bike.race.domain.drive.currentDrive.PauseCalculator
import com.bike.race.domain.drive.drivepath.DrivePathBuilder
import com.bike.race.domain.drive.drivepath.DrivePathFilter
import com.bike.race.domain.drive.drivepath.PathAngleDiffChecker
import com.bike.race.domain.drive.drivepath.SpeedDiffCalculator
import com.bike.race.domain.location.LocalityInfoCollector
import com.bike.race.domain.preference.UserPreferenceManager
import com.bike.race.domain.privacyPolicy.PrivacyPolicyService
import com.bike.race.repositories.DriveRepository
import com.bike.race.ui.compare.CompareViewModel
import com.bike.race.ui.dashboard.DashboardViewModel
import com.bike.race.ui.driveReport.DriveReportViewModel
import com.bike.race.ui.home.HomeViewModel
import com.bike.race.ui.myAllDrive.MyAllDrivesViewModel
import com.bike.race.ui.mydrive.MyDrivesViewModel
import com.bike.race.ui.splash.SplashViewModel
import com.bike.race.utils.ClockUtils
import com.bike.race.utils.ConversionUtil
import com.bike.race.utils.SphericalUtil
import com.bike.race.utils.StateViewProvider
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val koinModule = module {

    single { DriveService() }

    single { DriveLocalityAddService(get(), get()) }

    factory { PrivacyPolicyService(get()) }

    factory { StateViewProvider() }

    factory { LocationProvider() }

    factory { androidContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager }

    factory { ClockUtils() }

    factory { ConversionUtil() }

    factory { SphericalUtil() }

    factory { EndForgotCalculator() }

    factory { DriveRepository(get()) }

    factory { MapPolyLineCreator() }

    factory { DrivePathBuilder() }

    factory { PauseCalculator() }

    factory { PathAngleDiffChecker() }

    factory { SpeedDiffCalculator() }

    factory { DriveStatAnalyser() }

    factory { LocalityInfoCollector(androidContext()) }

    factory { UserPreferenceManager(androidContext()) }

    single { AppDatabase.invoke(androidContext()) }

    factory { SingleLocationProvider(get()) }

    factory { DrivePathFilter() }

    viewModel { HomeViewModel(get()) }

    viewModel { SplashViewModel(get()) }

    viewModel { DashboardViewModel() }

    viewModel { MyDrivesViewModel(get(), get()) }

    viewModel { DriveReportViewModel(get(), get(), get(), get(), get(), get()) }

    viewModel { MyAllDrivesViewModel(get()) }

    viewModel { CompareViewModel(get(), get()) }
}