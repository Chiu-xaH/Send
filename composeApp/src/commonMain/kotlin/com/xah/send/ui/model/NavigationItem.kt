package com.xah.send.ui.model

import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.DrawableResource

data class NavigationItem (
    val route: String,
    val label: String,
    val icon: DrawableResource,
    val filledIcon: DrawableResource
)
