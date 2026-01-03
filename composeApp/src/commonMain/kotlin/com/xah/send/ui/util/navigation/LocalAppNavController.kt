package com.xah.send.ui.util.navigation

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController

/**
 * App根导航
 */
val LocalAppNavController = staticCompositionLocalOf<NavHostController> {
    error("未提供根NavController")
}




