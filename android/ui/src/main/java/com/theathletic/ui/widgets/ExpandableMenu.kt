package com.theathletic.ui.widgets

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.Icon
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.utility.conditional

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T : Any> ExpandableMenu(
    modifier: Modifier = Modifier,
    dropdownModifier: Modifier = Modifier,
    options: List<T>,
    backgroundColor: Color,
    backgroundShape: Shape = RectangleShape,
    showDivider: Boolean = false,
    onItemClick: (T, Int) -> Unit = { _, _ -> },
    menuItem: @Composable (T, Int) -> Unit,
    menuDisplay: @Composable (Boolean) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }
    val dividerColor = AthTheme.colors.dark400
    val resourceColor = if (isExpanded) AthTheme.colors.dark400 else AthTheme.colors.dark700

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = !isExpanded },
        modifier = modifier,
    ) {
        menuDisplay(isExpanded)
        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = {
                isExpanded = false
            },
            modifier = Modifier
                .background(color = backgroundColor, shape = backgroundShape)
                .then(dropdownModifier)
        ) {
            options.forEachIndexed { index, option ->
                DropdownMenuItem(
                    modifier = Modifier.conditional(
                        showDivider && index != options.lastIndex
                    ) {
                        drawBehind {
                            drawLine(
                                color = dividerColor,
                                start = Offset(0f, size.height),
                                end = Offset(size.width, size.height),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                    },
                    onClick = {
                        isExpanded = false
                        onItemClick(option, index)
                    }
                ) {
                    menuItem(option, index)
                }
            }
        }
    }
}

@Composable
fun RoundedDropDownMenu(
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String, Int) -> Unit,
) {
    var isExpandedMenu by remember { mutableStateOf(false) }
    val resourceColor = if (isExpandedMenu) AthTheme.colors.dark400 else AthTheme.colors.dark700

    ExpandableMenu(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .border(1.dp, resourceColor, RoundedCornerShape(50)),
        dropdownModifier = Modifier.width(200.dp),
        backgroundColor = AthTheme.colors.dark300,
        backgroundShape = RoundedCornerShape(8.dp),
        options = options,
        showDivider = true,
        onItemClick = { option, index ->
            onOptionSelected(option, index)
        },
        menuItem = { option, _ ->
            ExpandableMenuDefaultItemLayout(
                menuItem = ExpandableMenuItem(
                    option,
                    textColor = AthTheme.colors.dark700,
                    textSize = 16.sp,
                )
            )
        }
    ) { isExpanded ->
        isExpandedMenu = isExpanded
        ExpandableMenuDropdown(
            isExpanded = isExpanded,
            selectedOption = selectedOption
        )
    }
}

@Composable
fun ExpandableMenuDefaultItemLayout(
    menuItem: ExpandableMenuItem,
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = modifier
    ) {
        Text(
            text = menuItem.text,
            color = menuItem.textColor,
            style = AthTextStyle.Calibre.Utility.Medium.Small.copy(
                textAlign = menuItem.textAlign,
                fontSize = menuItem.textSize
            ),
            modifier = Modifier
                .weight(1f)
                .then(textModifier)
        )
        menuItem.icon()
    }
}

@Composable
fun ExpandableMenuDefaultIcon(
    icon: ImageVector,
    iconSize: Dp,
    iconColor: Color
) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = iconColor,
        modifier = Modifier.size(iconSize)
    )
}

@Composable
fun ExpandableMenuDropdown(isExpanded: Boolean, selectedOption: String) {
    val resourceColor = if (isExpanded) AthTheme.colors.dark400 else AthTheme.colors.dark700
    val rotationValue = remember { Animatable(0f) }
    LaunchedEffect(isExpanded) {
        rotationValue.animateTo(if (isExpanded) 180f else 0f)
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = selectedOption,
            style = AthTextStyle.Calibre.Utility.Medium.Small.copy(
                textAlign = TextAlign.Center,
                color = resourceColor
            ),
            modifier = Modifier
                .padding(
                    start = 12.dp,
                    top = 6.dp,
                    bottom = 6.dp
                )
                .wrapContentWidth()
                .animateContentSize()
        )
        Icon(
            imageVector = Icons.Filled.KeyboardArrowDown,
            contentDescription = "",
            tint = resourceColor,
            modifier = Modifier
                .size(16.dp)
                .rotate(rotationValue.value)
        )
        Spacer(modifier = Modifier.padding(end = 8.dp))
    }
}

data class ExpandableMenuItem(
    val text: String,
    val textColor: Color,
    val textSize: TextUnit = 14.sp,
    val textAlign: TextAlign = TextAlign.Start,
    val icon: @Composable () -> Unit = { }
)

@Preview
@Composable
private fun ExpandableMenu_Preview() {
    AthleticTheme(lightMode = false) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(AthTheme.colors.dark800)
        ) {
            val options = listOf("First", "Second", "Third")
            var currentlySelected by remember { mutableStateOf(options[0]) }
            ExpandableMenu(
                dropdownModifier = Modifier.width(100.dp),
                options = options,
                backgroundColor = AthTheme.colors.dark300,
                onItemClick = { option, _ -> currentlySelected = option },
                menuItem = { option, _ ->
                    ExpandableMenuDefaultItemLayout(
                        menuItem = ExpandableMenuItem(
                            text = option,
                            textColor = AthTheme.colors.dark800,
                            textAlign = TextAlign.Justify,
                        ),
                    )
                }
            ) {
                OutlinedTextField(
                    value = currentlySelected,
                    onValueChange = {},
                    enabled = false,
                    textStyle = AthTextStyle.Calibre.Utility.Medium.Small.copy(
                        color = AthTheme.colors.dark800,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.background(AthTheme.colors.green)
                )
            }
        }
    }
}