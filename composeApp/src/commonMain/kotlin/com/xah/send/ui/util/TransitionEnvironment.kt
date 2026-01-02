package com.xah.send.ui.util

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController


// APP的根导航
val LocalAppNavController = staticCompositionLocalOf<NavHostController> {
    error("未提供根NavController")
}




