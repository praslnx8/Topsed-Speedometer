package com.bike.race.ui.home

import android.app.Activity
import android.app.PictureInPictureParams
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Bundle
import android.os.IBinder
import android.os.Messenger
import android.os.PersistableBundle
import android.util.Rational
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bike.race.R
import com.bike.race.components.service.ActivityMessenger
import com.bike.race.components.service.DriveBackGroundService
import com.bike.race.databinding.ActivityHomeBinding
import com.bike.race.ui.driveReport.DriveReportActivity
import com.bike.race.ui.home.DriveAction.*
import com.bike.race.uiModels.DashboardData
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeActivity : AppCompatActivity(), ServiceConnection {

    private val viewModel: HomeViewModel by viewModel()
    private lateinit var viewBinding: ActivityHomeBinding

    //TODO https://issuetracker.google.com/issues/142847973#comment1
    private val navController: NavController by lazy {
        (supportFragmentManager.findFragmentById(R.id.fragNavHost) as NavHostFragment).navController
    }

    private val activityMessenger =
        ActivityMessenger(
            ::onStatusUpdate,
            ::onDriveFinished
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        startService(Intent(this, DriveBackGroundService::class.java))

        viewBinding.bottomNavigation.setupWithNavController(navController)

        viewModel.startStopLiveData.observe(this) {
            when (it) {
                START -> {
                    activityMessenger.startDrive()
                    viewBinding.root.keepScreenOn = true
                }
                PAUSE -> {
                    activityMessenger.pauseDrive()
                    viewBinding.root.keepScreenOn = false
                }
                STOP -> {
                    activityMessenger.stopDrive()
                    viewBinding.root.keepScreenOn = false
                }
            }
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
        viewModel.syncServices()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        activityMessenger.onDisconnect()
    }

    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        activityMessenger.onConnect(Messenger(service))
        activityMessenger.handShake()
    }

    override fun onStart() {
        super.onStart()
        bindService(
            Intent(this, DriveBackGroundService::class.java),
            this,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun onStop() {
        super.onStop()
        unbindService(this)
    }

    private fun onStatusUpdate(dashboardData: DashboardData) {
        viewModel.updateDashboardData(dashboardData)
    }

    private fun onDriveFinished(raceId: Long) {
        openDriveReport(raceId)
    }

    override fun onUserLeaveHint() {
        checkAndEnterPipMode()
    }

    private fun checkAndEnterPipMode(): Boolean {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O
            && packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)
        ) {
            if (viewModel.dashboardLiveData.value?.isRunning() == true) {
                return enterPictureInPictureMode(
                    PictureInPictureParams.Builder()
                        .setAspectRatio(Rational(1, 1))
                        .build()
                )
            }
        }

        return false
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        if (isInPictureInPictureMode) {
            viewBinding.pipTextView.visibility = View.VISIBLE
            viewBinding.fragNavHost.visibility = View.GONE
            viewBinding.bottomNavigation.visibility = View.GONE
            viewModel.dashboardLiveData.observe(this) {
                viewBinding.pipTextView.text = it?.getCurrentSpeedText() ?: ""
            }
        } else {
            viewBinding.pipTextView.visibility = View.GONE
            viewBinding.fragNavHost.visibility = View.VISIBLE
            viewBinding.bottomNavigation.visibility = View.VISIBLE
            viewModel.dashboardLiveData.removeObservers(this)
        }
    }

    override fun onBackPressed() {
        if (!checkAndEnterPipMode()) {
            super.onBackPressed()
        }
    }

    private fun openDriveReport(raceId: Long) {
        DriveReportActivity.open(
            context = this,
            driveId = raceId,
        )
    }

    companion object {
        fun open(activity: Activity) {
            val intent = Intent(activity, HomeActivity::class.java)
            activity.startActivity(intent)
        }
    }
}
