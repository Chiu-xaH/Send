package com.xah.send.ui.screen.home.settings

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
import com.xah.send.logic.function.find.FinsDevicesHelper
import com.xah.send.logic.function.transfer.Receiver
import com.xah.send.logic.util.showToast
import com.xah.send.ui.componment.container.CustomCard
import com.xah.send.ui.componment.text.DividerTextExpandedWith
import com.xah.send.ui.componment.container.TransplantListItem
import com.xah.send.ui.componment.container.cardNormalColor

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
                        supportingContent = { Text(FinsDevicesHelper.DISCOVERY_PORT.toString()) },
                        modifier = Modifier.clickable {
                            showToast("正在开发")
                        }
                    )
                }
            }
            DividerTextExpandedWith("存储") {
                CustomCard (color = cardNormalColor()) {
                    TransplantListItem(
                        headlineContent = { Text("接收文件的保存路径") },
                        supportingContent = { Text(Receiver.saveDir.absolutePath) },
                        modifier = Modifier.clickable {
                            showToast("正在开发")
                        }
                    )
                }
            }
        }
    }

}