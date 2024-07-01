package com.theathletic.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthColor
import com.theathletic.themes.AthTheme
import com.theathletic.ui.utility.conditional

/**
 * A wrapper for [ModalBottomSheetLayout] that allows any content and uses a Boolean for visibility.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ModalBottomSheetLayout(
    content: @Composable () -> Unit,
    modalSheetContent: @Composable (() -> Unit),
    isVisible: Boolean,
    onDismissed: () -> Unit,
    modifier: Modifier = Modifier,
    expansionType: BottomSheetExpansionType = BottomSheetExpansionType.DEFAULT,
    sheetShape: Shape = BottomSheetShape,
    sheetBackgroundColor: Color = AthTheme.colors.dark200
) {
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    LaunchedEffect(isVisible) {
        if (isVisible) {
            sheetState.show()
        } else {
            sheetState.hide()
        }
    }
    LaunchedEffect(sheetState.isVisible) {
        if (!sheetState.isVisible) {
            onDismissed()
        }
    }
    val innerModifier = Modifier
        .conditional(expansionType != BottomSheetExpansionType.DEFAULT) {
            fillMaxHeight(expansionType.maxHeight)
        }
        .defaultMinSize(1.dp)
    ModalBottomSheetLayout(
        sheetState = sheetState,
        scrimColor = AthColor.Gray100.copy(alpha = .5f),
        sheetContent = {
            Column(innerModifier) {
                if (isVisible) {
                    modalSheetContent()
                } else {
                    Spacer(
                        Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                    )
                }
            }
        },
        sheetShape = sheetShape,
        sheetBackgroundColor = sheetBackgroundColor,
        content = content,
        modifier = modifier,
    )
}

/**
 * A wrapper for [ModalBottomSheetLayout] which interfaces with [ModalBottomSheetType] for simpler
 * use cases on our end. By using a sealed class marked with [ModalBottomSheetType], you just need
 * to account for the content of the current modal passed in and this will handle the hiding and
 * showing. Send in null to the currentModal parameter to hide the bottom sheet.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <Modal : ModalBottomSheetType> ModalBottomSheetLayout(
    currentModal: Modal?,
    onDismissed: () -> Unit,
    modalSheetContent: @Composable (ColumnScope.(Modal) -> Unit),
    modifier: Modifier = Modifier,
    expansionType: BottomSheetExpansionType = BottomSheetExpansionType.DEFAULT,
    sheetShape: Shape = BottomSheetShape,
    sheetBackgroundColor: Color = AthTheme.colors.dark200,
    content: @Composable () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    LaunchedEffect(currentModal) {
        if (currentModal == null) {
            sheetState.hide()
        } else {
            sheetState.show()
        }
    }
    LaunchedEffect(sheetState.isVisible) {
        if (!sheetState.isVisible) {
            onDismissed()
        }
    }
    val innerModifier = Modifier
        .conditional(expansionType != BottomSheetExpansionType.DEFAULT) {
            fillMaxHeight(expansionType.maxHeight)
        }
        .defaultMinSize(1.dp)
    ModalBottomSheetLayout(
        sheetState = sheetState,
        scrimColor = AthColor.Gray100.copy(alpha = .5f),
        sheetContent = {
            Column(innerModifier) {
                when (currentModal) {
                    null -> Spacer(
                        Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                    )
                    else -> modalSheetContent(currentModal)
                }
            }
        },
        sheetShape = sheetShape,
        sheetBackgroundColor = sheetBackgroundColor,
        content = content,
        modifier = modifier,
    )
}

@Composable
fun BottomSheetTopDragHandler() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.Center)
            .padding(top = 8.dp, bottom = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(50.dp, 5.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(AthTheme.colors.dark500)
        )
    }
}

private val BottomSheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)

enum class BottomSheetExpansionType(val maxHeight: Float) {
    HALF_EXPANSION_SUPPORT(0.98f),
    DEFAULT(0.0f)
}

/**
 * A marker type which allows you to interface with [ModalBottomSheetLayout]. By marking a sealed
 * class with this interface, you can use that sealed class to represent different bottom sheets
 * that you might show on a particular screen.
 */
interface ModalBottomSheetType