package com.bike.race.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.MutableLiveData
import com.bike.race.BuildConfig
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes

object LocationPermissionUtils {

    private val locPermissionLiveData = MutableLiveData<LocPermissionStatus>()

    fun compute(context: Context) {
        val locationPermission = PermissionChecker.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PermissionChecker.PERMISSION_GRANTED

        val locationEnabled = isLocationEnabled(context)

        locPermissionLiveData.value = LocPermissionStatus(
            locationPermission = locationPermission,
            locationEnabled = locationEnabled
        )
    }


    fun isBasicPermissionGranted(context: Context): Boolean {
        return PermissionChecker.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PermissionChecker.PERMISSION_GRANTED
    }

    fun shouldShowRationaleBasicPermission(activity: Activity): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    fun getBasicPermissions(): Array<String> {
        return arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    fun getAppPermissionSettingPageIntent(): Intent {
        return Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:" + BuildConfig.APPLICATION_ID)
        )
    }

    /**
     * Request google play service to enable android location.
     *
     * @param activity Activity context who requested.
     */
    fun askEnableLocationRequest(
        activity: Activity,
        locEnableCallBack: (status: Boolean) -> Unit
    ) {
        if (isGooglePlayInstalled(activity)) {

            val locationRequest =
                LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)
            val result = LocationServices.getSettingsClient(activity)
                .checkLocationSettings(builder.build())

            result.addOnCompleteListener { task ->
                try {
                    val response = task.getResult(ApiException::class.java)
                    locEnableCallBack.invoke(
                        response?.locationSettingsStates?.isLocationUsable ?: false
                    )
                } catch (exception: ApiException) {

                    if (exception.statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                        try {
                            val resolvable = exception as ResolvableApiException?
                            resolvable?.startResolutionForResult(activity, 21)
                        } catch (e: IntentSender.SendIntentException) {
                            // Ignore the error.
                        }
                    } else if (exception.statusCode == LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE) {
                        openDialogForEnableLocation(activity)
                    }
                }
            }
        } else {
            openDialogForEnableLocation(activity)
        }
    }

    private fun openDialogForEnableLocation(ctx: Context) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(ctx)
        val action = Settings.ACTION_LOCATION_SOURCE_SETTINGS
        val message = "Please enable location to continue"

        builder.setMessage(message)
            .setPositiveButton("OK") { _, _ -> ctx.startActivity(Intent(action)) }
        builder.create().show()
    }

    /**
     * Check for android location is enabled.
     *
     * @param context Context to access android components.
     * @return True if location is enabled.
     */
    fun isLocationEnabled(context: Context): Boolean {
        val lm: LocationManager? =
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager?

        return lm?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: false
    }

    private const val labelText = "Market"

    /**
     * Check for google play installed there by determining google play services.
     *
     * @param context Context to access android components.
     * @return true if google play service is available.
     */
    private fun isGooglePlayInstalled(context: Context): Boolean {
        val pm = context.packageManager
        try {
            val info = pm.getPackageInfo("com.android.vending", PackageManager.GET_ACTIVITIES)
            val label = info.applicationInfo.loadLabel(pm) as String
            return label != labelText
        } catch (e: PackageManager.NameNotFoundException) {

        }

        return false
    }

    data class LocPermissionStatus(
        val locationPermission: Boolean,
        val locationEnabled: Boolean
    )

}