package com.theathletic.ui.widgets

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme

@Composable
fun SearchTextField(
    searchText: String,
    @StringRes placeholderRes: Int,
    @DrawableRes leadingIconRes: Int,
    @DrawableRes trailingIconRes: Int,
    onUpdateSearchText: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = searchText,
        onValueChange = { onUpdateSearchText(it) },
        textStyle = AthTextStyle.Calibre.Utility.Regular.Large,
        singleLine = true,
        maxLines = 1,
        placeholder = {
            Text(
                text = stringResource(id = placeholderRes),
                style = AthTextStyle.Calibre.Utility.Regular.Large,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        leadingIcon = {
            ResourceIcon(
                resourceId = leadingIconRes,
                tint = AthTheme.colors.dark500
            )
        },
        trailingIcon = {
            if (searchText.isNotEmpty()) {
                IconButton(onClick = { onUpdateSearchText("") }) {
                    ResourceIcon(
                        resourceId = trailingIconRes,
                        tint = AthTheme.colors.dark800
                    )
                }
            }
        },
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.textFieldColors(
            textColor = AthTheme.colors.dark800,
            backgroundColor = AthTheme.colors.dark300,
            cursorColor = AthTheme.colors.dark800,
            placeholderColor = AthTheme.colors.dark500,
            leadingIconColor = AthTheme.colors.dark500,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        modifier = modifier
    )
}