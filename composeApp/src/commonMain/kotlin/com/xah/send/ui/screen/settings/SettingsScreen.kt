package com.xah.send.ui.screen.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.xah.send.logic.util.DISCOVERY_PORT
import com.xah.send.logic.util.showToast
import com.xah.send.ui.componment.CustomCard
import com.xah.send.ui.componment.DividerTextExpandedWith
import com.xah.send.ui.componment.TransplantListItem
import com.xah.send.ui.componment.cardNormalColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val scrollState = rememberScrollState()
    Scaffold(

    ) { innerPadding ->
        Column (
            modifier = Modifier
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            DividerTextExpandedWith("网络") {
                CustomCard (color = cardNormalColor()) {
                    TransplantListItem(
                        headlineContent = { Text("修改广播端口") },
                        supportingContent = { Text(DISCOVERY_PORT.toString()) },
                        modifier = Modifier.clickable {
                            showToast("正在开发")
                        }
                    )
                }
            }
        }
    }

}