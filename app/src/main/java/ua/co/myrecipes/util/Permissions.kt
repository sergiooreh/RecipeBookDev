package ua.co.myrecipes.util

import android.content.Context
import pub.devrel.easypermissions.EasyPermissions

object Permissions {
    fun hasStoragePermissions(context: Context) =
        EasyPermissions.hasPermissions(
            context,
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )
}