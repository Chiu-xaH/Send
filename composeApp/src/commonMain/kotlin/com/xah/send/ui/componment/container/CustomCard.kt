package com.xah.send.ui.componment

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xah.send.ui.componment.divider.PaddingHorizontalDivider


data class CardBottomButton(
    val text : String,
    val show : Boolean = true,
    val clickable :  (() -> Unit)? = null
)

private val CARD_BOTTOM_BUTTON_SIZE = 14.sp

@Composable
fun ColumnScope.CardBottomButtons(buttons : List<CardBottomButton>) {
    PaddingHorizontalDivider()
    LazyRow (modifier = Modifier.align(Alignment.End).padding(horizontal = APP_HORIZONTAL_DP)) {
        items(buttons.size,key = { it }) { index ->
            val bean = buttons[index]
            with(bean) {
                if (show) {
                    Spacer(Modifier.width(APP_HORIZONTAL_DP))
                    Text(
                        text = text,
                        color =
                            if (clickable == null)
                                MaterialTheme.colorScheme.onSurface
                            else {
                                if (text.contains("删除")) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.primary
                                }
                            },
                        fontSize = CARD_BOTTOM_BUTTON_SIZE,
                        modifier = Modifier
                            .padding(vertical = APP_HORIZONTAL_DP - 5.dp)
                            .let {
                                clickable?.let { click ->
                                    it.clickable { click() }
                                } ?: it
                            }
                    )
                }
            }
        }
    }
}


@Composable
fun CustomCard(
    modifier: Modifier = Modifier,
    color : Color? = null,
    shadow : Dp = 0.dp,
    shape: Shape = MaterialTheme.shapes.medium,
    border : BorderStroke? = null,
    content: @Composable () -> Unit
) {
    val baseModifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = APP_HORIZONTAL_DP, vertical = CARD_NORMAL_DP)

    Card(
        border = border,
        modifier = baseModifier.then(modifier),
        shape = shape,
        elevation =  CardDefaults. cardElevation(shadow),
        colors = if(color == null) CardDefaults.cardColors() else CardDefaults.cardColors(containerColor = color)
    ) {
        content()
    }
}


@Composable
fun mixedCardNormalColor(): Color {
    val overlay = cardNormalColor()
    val base = MaterialTheme.colorScheme.surface
    return overlay.compositeOver(base)
}
// 小卡片
@Composable
fun SmallCard(
    modifier: Modifier = Modifier.fillMaxSize(),
    color : Color? = null,
    shadow : Dp = 0.dp,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        elevation = CardDefaults.cardElevation(shadow),
        colors = CardDefaults.cardColors(containerColor = color ?: cardNormalColor())
    ) {
        content()
    }
}

val SEARCH_FUC_CARD_HEIGHT = 72.dp
@Composable
fun TransplantListItem(
    headlineContent :  @Composable () -> Unit,
    overlineContent  : @Composable() (() -> Unit)? = null,
    supportingContent : @Composable() (() -> Unit)? = null,
    trailingContent : @Composable() (() -> Unit)? = null,
    leadingContent : @Composable() (() -> Unit)? = null,
    colors : Color? = null,
    usePadding : Boolean = true,
    modifier: Modifier = Modifier
) {
    ListItem(
        headlineContent = headlineContent,
        overlineContent = overlineContent,
        supportingContent = supportingContent,
        colors = ListItemDefaults.colors(containerColor = colors ?: Color.Transparent) ,
        trailingContent = trailingContent,
        leadingContent = leadingContent,
        modifier = modifier.padding(horizontal =
            if(leadingContent == null) {
                if (usePadding) 2.dp else 0.dp
            } else {
                0.dp
            }
        )
    )
}

@Composable
private fun PCardListItem(
    headlineContent :  @Composable () -> Unit,
    overlineContent  : @Composable() (() -> Unit)? = null,
    supportingContent : @Composable() (() -> Unit)? = null,
    trailingContent : @Composable() (() -> Unit)? = null,
    leadingContent : @Composable() (() -> Unit)? = null,
    color : Color? = null,
    shape: Shape = MaterialTheme.shapes.medium,
    shadow: Dp = 0.dp,
    modifier: Modifier = Modifier,
    cardModifier : Modifier = Modifier
) {
    CustomCard( color = color, modifier = cardModifier, shape = shape, shadow = shadow) {
        TransplantListItem(
            headlineContent = headlineContent,
            overlineContent = overlineContent,
            supportingContent = supportingContent,
            trailingContent = trailingContent,
            leadingContent = leadingContent,
            usePadding = false,
            modifier = modifier
        )
    }
}


@Composable
fun CardListItem(
    headlineContent :  @Composable () -> Unit,
    overlineContent  : @Composable() (() -> Unit)? = null,
    supportingContent : @Composable() (() -> Unit)? = null,
    trailingContent : @Composable() (() -> Unit)? = null,
    leadingContent : @Composable() (() -> Unit)? = null,
    color : Color? = null,
    shape: Shape = MaterialTheme.shapes.medium,
    shadow: Dp = 0.dp,
    modifier: Modifier = Modifier,
    cardModifier: Modifier = Modifier,
) {
    PCardListItem(
        headlineContent, overlineContent, supportingContent, trailingContent,leadingContent, modifier = modifier, cardModifier = cardModifier,
        color = color ?: cardNormalColor(),
        shape = shape, shadow = shadow
    )
}

@Composable
fun cardNormalColor() : Color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = .05f)
@Composable
fun largeCardColor() : Color = MaterialTheme.colorScheme.surfaceVariant


