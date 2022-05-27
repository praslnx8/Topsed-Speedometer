package com.bike.race.ui.driveReport

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bike.race.R
import com.bike.race.databinding.ActivityDriveReportBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class DriveReportActivity : AppCompatActivity() {

    private var driveId: Long = 0L
    private val viewModel: DriveReportViewModel by viewModel()

    //TODO https://issuetracker.google.com/issues/142847973#comment1
    private val navController: NavController by lazy {
        (supportFragmentManager.findFragmentById(R.id.fragNavHost) as NavHostFragment).navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewBinding = ActivityDriveReportBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        driveId = intent.getLongExtra(DRIVE_ID_KEY, -1L)
        viewModel.setInitData(driveId)

        setSupportActionBar(viewBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewBinding.navigation.setupWithNavController(navController)

        viewModel.checkAndUpdateDrive()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        finish()
    }

    companion object {

        private const val DRIVE_ID_KEY = "DRIVE_ID_KEY"

        fun open(context: Context, driveId: Long) {
            val intent = Intent(context, DriveReportActivity::class.java).also {
                it.putExtra(DRIVE_ID_KEY, driveId)
            }
            context.startActivity(intent)
        }
    }
}