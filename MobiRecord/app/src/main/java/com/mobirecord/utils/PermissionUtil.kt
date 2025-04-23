package com.mobirecord.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionUtil {


    companion object {
        var Location = 3;

        fun ShowSingleButtonDialog(
            activity: Context?,
            title: String?,
            message: String?,
            buttontext: String?
        ) {
            val alert = AlertDialog.Builder(
                activity!!
            )
            alert.setMessage(message)
            alert.setTitle(title)
            alert.setPositiveButton(
                buttontext
            ) { dialog, which ->
                dialog.dismiss()
            }
            alert.create()
            alert.show()
        }

        fun checkStoragePermission(activity: Activity?): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                Log.e("TAG", "checkWriteExternalPermission UPSIDE_DOWN_CAKE: ")
                val readMediaVisualSelectedPermission =
                    activity?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED) }
                val readImagesPermission =
                    activity?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.READ_MEDIA_IMAGES) }
                val readVideosPermission =
                    activity?.let { ContextCompat.checkSelfPermission(it, Manifest.permission.READ_MEDIA_VIDEO) }
                return readMediaVisualSelectedPermission == PackageManager.PERMISSION_GRANTED || readImagesPermission == PackageManager.PERMISSION_GRANTED|| readVideosPermission == PackageManager.PERMISSION_GRANTED

            }
            else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                if (ContextCompat.checkSelfPermission(
                        activity!!,
                        Manifest.permission.READ_MEDIA_IMAGES
                    ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                        activity,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                ){
                    return true
                }
            }else{
                return/* if (ContextCompat.checkSelfPermission(
                        activity!!,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_DENIED
                ) {
                    false
                } else*/ if (ContextCompat.checkSelfPermission(
                        activity!!,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_DENIED
                ) {
                    false
                } else ContextCompat.checkSelfPermission(
                    activity!!,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_DENIED
            }

            return false
        }

        fun checkCameraPermission(activity: Activity?): Boolean {
            activity?.let {
                // Check for camera permission only
                return ContextCompat.checkSelfPermission(
                    it, Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            }
            return false
        }
        fun checkReadMediaPermission(activity: Activity?): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                if (ContextCompat.checkSelfPermission(
                        activity!!,
                        Manifest.permission.READ_MEDIA_IMAGES
                    ) == PackageManager.PERMISSION_GRANTED
                ){
                    return true
                }
            }else{
                return if (ContextCompat.checkSelfPermission(
                        activity!!,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    true
                } else {
                    false
                }
            }

            return false
        }

        fun checkingAllPermissions(activity: Activity?, PERMISSIONS: Array<String>?): Boolean {
            val status = false
            val PERMISSION_ALL = 1
            //        String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA};
            return if (!hasPermissions(activity, PERMISSIONS.toString())) {
                ActivityCompat.requestPermissions(activity!!, PERMISSIONS!!, PERMISSION_ALL)
                false
            } else {
                true
            }
        }


        fun checkLocation(activity: Activity?): Boolean {
            val netPermission =
                ContextCompat.checkSelfPermission(
                    activity!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            return netPermission == PackageManager.PERMISSION_GRANTED
        }

        fun hasPermissions(context: Context?, vararg permissions: String?): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
                for (permission in permissions) {
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            permission!!
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return false
                    }
                }
            }
            return true
        }

        fun requestPermissions(activity: Activity?): Boolean {
            val locationPermission: Boolean = checkLocation(activity)
            return if (!locationPermission /*&&!contactsPermission*/) {
                val PERMISSIONS = arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                checkingAllPermissions(activity, PERMISSIONS)
                false
            } else {
                true
            }
        }

    }


}