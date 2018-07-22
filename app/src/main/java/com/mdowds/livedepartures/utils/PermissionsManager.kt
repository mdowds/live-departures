package com.mdowds.livedepartures.utils

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat

interface PermissionsManager {
    fun isPermissionGranted(requestingActivity: Activity, permission: String): Boolean
    fun requestPermissions(requestingActivity: Activity, permission: String)
}

class DevicePermissionsManager: PermissionsManager {

    companion object {
        const val PERMISSIONS_REQUEST_CODE = 1
    }

    override fun isPermissionGranted(requestingActivity: Activity, permission: String) =
            ActivityCompat.checkSelfPermission(requestingActivity, permission) == PackageManager.PERMISSION_GRANTED

    override fun requestPermissions(requestingActivity: Activity, permission: String) =
            ActivityCompat.requestPermissions(requestingActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_CODE
            )
}