package com.bike.race.ui.permission

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bike.race.R
import com.bike.race.databinding.ActivityLocationPermissionCheckBinding
import com.bike.race.utils.ConsoleLog
import com.bike.race.utils.DialogUtils
import com.bike.race.utils.LocationPermissionUtils

class PermissionCheckActivity : AppCompatActivity() {

    private val reqForLocation = 21
    private var isPromptMode = false
    private var mayBeDenied = false

    private val locationSettingActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                LocationPermissionUtils.compute(this)
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        isPromptMode = intent.getBooleanExtra(IS_PROMPT_MODE, false)

        if (isPromptMode) {
            checkAndAsk()
        } else {
            val viewBinding = ActivityLocationPermissionCheckBinding.inflate(layoutInflater)
            setContentView(viewBinding.root)
            viewBinding.permissionMessage.text = getString(R.string.bg_permission_message)

            viewBinding.allowBtn.setOnClickListener {
                checkAndAsk()
            }

            viewBinding.cancelBtn.setOnClickListener {
                cancel()
            }
        }
    }

    private fun askPermissionWithExplanation() {
        val message = getString(
            R.string.need_your_bg_location_permission
        )

        DialogUtils.createDialog(
            context = this,
            title = getString(R.string.location_permission),
            message = message,
            positiveAction = getString(R.string.grant_access),
            negativeAction = getString(R.string.cancel),
            onSuccessAction = {
                if (shouldShowRationalePermission()) {
                    askPermission()
                } else {
                    locationSettingActivityResultLauncher.launch(LocationPermissionUtils.getAppPermissionSettingPageIntent())
                }
            },
            onNegativeAction = {
                cancel()
            }).show()
    }

    private fun check() = LocationPermissionUtils.isBasicPermissionGranted(this)

    private fun checkAndAsk() {

        if (check()) {
            LocationPermissionUtils.askEnableLocationRequest(this, ::locationEnabled)
        } else {
            if (shouldShowRationalePermission()) {
                askPermissionWithExplanation()
            } else {
                mayBeDenied = true
                askPermission()
            }
        }
    }

    private fun askPermission() {
        val permissions = LocationPermissionUtils.getBasicPermissions()

        requestPermissions(permissions, reqForLocation)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == reqForLocation) {
            if (check()) {
                if (LocationPermissionUtils.isLocationEnabled(this)) {
                    proceed()
                } else {
                    LocationPermissionUtils.askEnableLocationRequest(this, ::locationEnabled)
                }
            } else {
                if (!shouldShowRationalePermission()) {
                    askPermissionWithExplanation()
                } else {
                    cancel()
                }
            }
        }
    }

    private fun shouldShowRationalePermission() =
        LocationPermissionUtils.shouldShowRationaleBasicPermission(this)

    private fun locationEnabled(ignore: Boolean) {
        ConsoleLog.i("LOC", ignore.toString())
        checkAndProceed()
    }

    override fun onResume() {
        super.onResume()
        checkAndProceed()
    }

    private fun checkAndProceed() {
        if (check() && LocationPermissionUtils.isLocationEnabled(this)) {
            proceed()
        }
    }

    private fun proceed() {
        LocationPermissionUtils.compute(this)
        setResult(RESULT_OK)
        finish()
    }

    private fun cancel() {
        finish()
    }

    companion object {

        private const val IS_PROMPT_MODE = "IS_PROMPT_MODE"

        fun getOpenIntent(
            context: Context,
            isPromptMode: Boolean = false
        ): Intent {
            val intent = Intent(context, PermissionCheckActivity::class.java)
            intent.putExtra(IS_PROMPT_MODE, isPromptMode)
            return intent
        }
    }
}
