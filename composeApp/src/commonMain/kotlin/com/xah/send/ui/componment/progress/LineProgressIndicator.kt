package com.xah.send.ui.componment.progress

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xah.send.ui.util.Constant.APP_HORIZONTAL_DP
import kotlin.math.roundToInt

@Composable
fun CustomLineProgressIndicator(
    value : Float,
    text : String? = "${(value*100f).roundToInt()}%",
    color : Color = ProgressIndicatorDefaults.linearColor,
    trackColor : Color = ProgressIndicatorDefaults. linearTrackColor,
    height : Dp = 20.dp,
    modifier: Modifier = Modifier.fillMaxWidth().padding(horizontal = APP_HORIZONTAL_DP)
) {
    val textColor = if (value > 0.56f) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    Box(
        modifier = modifier
            .height(height)
            .clip(MaterialTheme.shapes.extraSmall)
    ) {
        LinearProgressIndicator(
            modifier = Modifier.matchParentSize(),
            color = color,
            trackColor = trackColor,
            strokeCap = StrokeCap.Butt,
            progress = { value },
            drawStopIndicator = {},
            gapSize = 0.dp
        )
        // 文字覆盖在进度条中间
        text?.let {
            Text(
                text = it,
                color = textColor,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier
                    .align(
                        if(value in 0.44..0.56) {
                            Alignment.CenterEnd
                        } else {
                            Alignment.Center
                        }
                    ).padding(horizontal = APP_HORIZONTAL_DP)
            )
        }
    }
}


