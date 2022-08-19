package com.ash.thetracker.shared.permissions

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.ash.thetracker.R
import com.ash.thetracker.shared.DialogUtils
import com.ash.thetracker.shared.string

class MultiplePermissionChecker(
    private val permissionListener: PermissionListener,
    private val activity: AppCompatActivity
) {
    private val permissionsArray = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACTIVITY_RECOGNITION)
    } else {
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    private var registerPermission: ActivityResultLauncher<Array<String>>? = null
    private fun registerPermission(): ActivityResultLauncher<Array<String>> = activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        val fineLocationGranted = result[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = result[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        val activityRecognitionGranted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            result[Manifest.permission.ACTIVITY_RECOGNITION] ?: false
        } else false

        when {
            fineLocationGranted -> {
                permissionListener.locationGranted()
            }
            coarseLocationGranted -> {
                permissionListener.locationGranted()
            }
            else -> {
                permissionListener.locationNonGranted()
                showDialogForPermission()
            }
        }
        when {
            activityRecognitionGranted -> {
                permissionListener.activityRecognitionGranted()
            }
            else -> {
                showDialogForPermission()
                permissionListener.activityRecognitionNonGranted()
            }
        }
    }

    fun launchPermissionChecking() {
        if (registerPermission == null) registerPermission = registerPermission().apply { launch(permissionsArray) }
        else {
            registerPermission?.launch(permissionsArray)
        }
    }

    private fun showDialogForPermission() {
        DialogUtils.showDialog(
            context = activity, title = activity.string(R.string.permission_required), message = activity.string(R.string.give_us_permission),
            positiveButtonText = activity.string(R.string.change_settings), showOnlyPositiveButton = true, positiveButtonClickListener = {
                val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", activity.packageName, null)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                activity.startActivity(settingsIntent)
            }
        )
    }

    interface PermissionListener {
        fun locationGranted()
        fun locationNonGranted()
        fun activityRecognitionGranted()
        fun activityRecognitionNonGranted()
    }

    fun isLocationPermissionGranted() = (ContextCompat.checkSelfPermission(activity, permissionsArray[0]) == PackageManager.PERMISSION_GRANTED)
            || (ContextCompat.checkSelfPermission(activity, permissionsArray[1]) == PackageManager.PERMISSION_GRANTED)

    fun isActivityRecognitionPermissionGranted() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        ContextCompat.checkSelfPermission(activity, permissionsArray[2]) == PackageManager.PERMISSION_GRANTED
    } else false

}