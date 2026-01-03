package com.xah.send.activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.core.view.WindowCompat
import com.xah.send.logic.util.android.PermissionSetter.checkAndRequestStoragePermission
import com.xah.send.ui.style.TransparentSystemBars
import com.xah.send.ui.screen.App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        //竖屏
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContent {
            LaunchedEffect(Unit) {
                checkAndRequestStoragePermission(this@MainActivity)
            }
            TransparentSystemBars()
            App()
        }
    }
}