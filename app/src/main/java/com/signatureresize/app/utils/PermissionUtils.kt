package com.signatureresize.app.utils

import android.Manifest
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestStoragePermissions(
    onPermissionGranted: () -> Unit
) {
    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(
            Manifest.permission.READ_MEDIA_IMAGES
        )
    } else {
        listOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }

    val permissionState = rememberMultiplePermissionsState(permissions = permissions)

    if (permissionState.allPermissionsGranted) {
        onPermissionGranted()
    } else {
        SideEffect {
            permissionState.launchMultiplePermissionRequest()
        }
    }
}
