package com.xah.send.ui.componment

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xah.send.ui.style.align.ColumnVertical
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

val CARD_NORMAL_DP : Dp = 2.5.dp


@Composable
fun LargeButton(
    modifier : Modifier = Modifier,
    iconModifier : Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color =  Color.Unspecified,
    contentColor : Color =  Color.Unspecified,
    icon : DrawableResource,
    text : String,
    shape : Shape = MaterialTheme.shapes.medium,
    onClick : () -> Unit,
    ) {
    Button(
        enabled = enabled,
        modifier = modifier,
        shape = shape,
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = containerColor, contentColor = contentColor)
    ) {
        ColumnVertical {
            Icon(painterResource(icon),null,modifier = iconModifier)
            Spacer(modifier = Modifier.height(CARD_NORMAL_DP))
            ScrollText(text)
        }
    }
}