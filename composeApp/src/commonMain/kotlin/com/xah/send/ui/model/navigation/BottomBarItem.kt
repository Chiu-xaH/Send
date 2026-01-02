package com.xah.send.ui.model.navigation

import org.jetbrains.compose.resources.DrawableResource

data class BottomBarItem (
    val route: String,
    val label: String,
    val icon: DrawableResource,
    val filledIcon: DrawableResource
)