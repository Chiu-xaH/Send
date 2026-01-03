package com.xah.send.ui.componment.button

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.xah.send.ui.util.navigation.LocalAppNavController
import org.jetbrains.compose.resources.painterResource
import send.composeapp.generated.resources.Res
import send.composeapp.generated.resources.arrow_back


@Composable
fun NavigationBackButton() {
    val navController = LocalAppNavController.current
    IconButton(
        onClick = {
            navController.popBackStack()
        }
    ) {
        Icon(
            painterResource(Res.drawable.arrow_back),
            null,
            tint = MaterialTheme.colorScheme.primary
        )
    }
}
