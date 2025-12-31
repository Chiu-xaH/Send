package com.xah.send.ui.screen.send

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.xah.send.ui.componment.APP_HORIZONTAL_DP
import com.xah.send.ui.componment.CARD_NORMAL_DP
import com.xah.send.ui.componment.DividerTextExpandedWith
import com.xah.send.ui.componment.LargeButton
import com.xah.send.ui.screen.receive.CustomFloatingActionButtonShadow
import send.composeapp.generated.resources.Res
import send.composeapp.generated.resources.content_paste
import send.composeapp.generated.resources.files
import send.composeapp.generated.resources.keyboard_alt

private val buttonPadding = CARD_NORMAL_DP*4

@Composable
fun SendScreen() {
    val scrollState = rememberScrollState()
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton (
                onClick = {
                    // TODO
                },
                elevation = CustomFloatingActionButtonShadow()
            ) {
                Text("重新扫描设备")
            }
        }
    ) { innerPadding ->
        Column (
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            DividerTextExpandedWith("发送内容") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = APP_HORIZONTAL_DP)
                ) {
                    // TODO 文件
                    LargeButton(
                        icon = Res.drawable.files,
                        text = "文件",
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1/3f)
                            .padding(end = buttonPadding)
                    ) {

                    }
                    // TODO 文本
                    LargeButton(
                        icon = Res.drawable.keyboard_alt,
                        text = "文本",
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1/3f)
                            .padding(end = buttonPadding)
                    ) {

                    }
                    // TODO 剪切板
                    LargeButton(
                        icon = Res.drawable.content_paste,
                        text = "剪切板",
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1/3f)
                    ) {

                    }
                }
            }
            DividerTextExpandedWith("发送至") {

            }
        }
    }
}