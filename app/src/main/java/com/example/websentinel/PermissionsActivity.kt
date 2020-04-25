package com.example.websentinel

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener


class PermissionsActivity : AppCompatActivity() {
    private var btnGrant: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        //Init the layout
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_permissions)

        //if the permissions are already granted, jump to the main activity
        if (checkPermissions(Manifest.permission.INTERNET,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION)) {
            startActivity(Intent(this@PermissionsActivity, GoogleMapsActivity::class.java))
            Log.w("cevaTag", "PermissionsActivity switches to GoogleMapsActivity")
            this.finish() //finish activity for device performance optimization
        }

        //Init the grant access button
        btnGrant = findViewById(R.id.btn_grant)
        btnGrant?.setOnClickListener {
            Dexter.withActivity(this)
                .withPermissions(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        report?.let {
                            if (report.areAllPermissionsGranted()) {
                                Log.w("cevaTag", "All permissions granted")
                                startActivity(Intent(this@PermissionsActivity, GoogleMapsActivity::class.java))
                                finish() //finish applies to the PermissionListener object
                            }
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest>?, token: PermissionToken?) {
                        token?.continuePermissionRequest()
                    }
                })
                .check()
        }
    }

    private fun checkPermissions(vararg permissions:String): Boolean {
        permissions.forEach { if(ContextCompat.checkSelfPermission(this@PermissionsActivity, it)==PackageManager.PERMISSION_DENIED)
            return false
        }
        return true
    }
}