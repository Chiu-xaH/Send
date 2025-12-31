package com.xah.send.ui.util

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

// 导航后 上一级永远是firstRoute
fun NavController.navigateForBottomBar(route: String) {
    navigate(route) {
        popUpTo(graph.startDestinationId) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun NavController.currentRouteWithArgWithoutValues() : String? = this.currentBackStackEntryAsState().value?.destination?.route

@Composable
fun NavController.currentRouteWithoutArgs() : String? =  currentRouteWithArgWithoutValues()?.substringBefore("?")

@Composable
fun NavController.isCurrentRouteWithoutArgs(route: String) : Boolean = currentRouteWithoutArgs() == route.substringBefore("?")

// 得到上一级
fun NavController.previousRouteWithArgWithoutValues(): String? = this.previousBackStackEntry?.destination?.route

@Composable
fun NavController.previousRouteWithoutArgs() : String? =  previousRouteWithArgWithoutValues()?.substringBefore("?")

// 所有
fun NavController.allRouteStack() : List<String> = this.currentBackStack.value.mapNotNull { it.destination.route }
