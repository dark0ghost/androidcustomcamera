package org.dark0ghost.camera.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission

fun isAcceptCamera(context: Context): Boolean = checkSelfPermission(context,Manifest.permission.CAMERA) != PermissionChecker.PERMISSION_GRANTED

fun requestCameraPermission(activity: Activity): Unit =
    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA),1)