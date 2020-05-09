package com.example.doroute

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

object PermissionsManagerObject{
    fun dexter(activity: Activity){
        Dexter.withActivity(activity)
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.INTERNET)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if (report.areAllPermissionsGranted()) {
                            Log.w("Permissions Manager", "All permissions granted")
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>?, token: PermissionToken?) {
                    token?.continuePermissionRequest()
                }
            })
            .check()
    }

    fun checkPermissions(context: Activity, vararg permissions:String): Boolean {
        permissions.forEach { if(ContextCompat.checkSelfPermission(context, it)== PackageManager.PERMISSION_DENIED)
            return false
        }
        return true
    }
}