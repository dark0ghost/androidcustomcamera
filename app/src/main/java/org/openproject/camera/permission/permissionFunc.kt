package org.openproject.camera.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission

fun isAcceptCamera(context: Context): Boolean = checkSelfPermission(context,Manifest.permission.CAMERA) != PermissionChecker.PERMISSION_GRANTED
fun isAcceptNFC(context: Context): Boolean = checkSelfPermission(context,Manifest.permission.NFC) != PermissionChecker.PERMISSION_GRANTED
fun isAcceptWriteExternalStorage(context: Context): Boolean =
        checkSelfPermission(context,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED
fun isAcceptRecordAudio(context: Context): Boolean = checkSelfPermission(context,Manifest.permission.RECORD_AUDIO) != PermissionChecker.PERMISSION_GRANTED
fun isAcceptInternet(context: Context): Boolean = checkSelfPermission(context,Manifest.permission.INTERNET) != PermissionChecker.PERMISSION_GRANTED
fun requestCameraPermission(activity: Activity): Unit =
    ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.CAMERA),1)