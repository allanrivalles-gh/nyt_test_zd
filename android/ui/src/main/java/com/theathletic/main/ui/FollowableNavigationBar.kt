package com.theathletic.main.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.theathletic.followable.Followable
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.R
import com.theathletic.ui.utility.athPlaceholder
import com.theathletic.ui.widgets.RemoteImageAsync
import com.theathletic.ui.widgets.ResourceIcon

@Composable
fun FollowableNavigationBar(
    navItems: List<NavigationItem>,
    showPlaceholder: Boolean = false,
    showEdit: Boolean = true,
    showAddIfEmpty: Boolean = false,
    bottomPadding: Dp = 8.dp,
    onEditClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    onFollowableClick: (Followable.Id, Int) -> Unit,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
            .padding(top = 12.dp, bottom = bottomPadding)
    ) {
        if (showEdit) item { Edit(onClick = onEditClick) }

        itemsIndexed(navItems) { index, navItem ->
            NavigationItem(
                id = navItem.id,
                title = navItem.title,
                imageUrl = navItem.imageUrl,
                placeholder = navItem.placeholder,
                background = navItem.background,
                showPlaceholder = showPlaceholder,
                onClick = { id -> onFollowableClick(id, index) }
            )
        }

        if (showAddIfEmpty) item { Add(onClick = onAddClick) }
    }
}

@Composable
private fun Edit(
    onClick: () -> Unit
) {
    NavItem(
        text = stringResource(R.string.global_edit),
        image = {
            ResourceIcon(
                resourceId = R.drawable.ic_edit,
                tint = AthTheme.colors.dark700,
                modifier = Modifier.size(24.dp)
            )
        },
        onClick = onClick,
        showPlaceholder = false,
    )
}

@Composable
private fun Add(
    onClick: () -> Unit
) {
    NavItem(
        text = stringResource(R.string.global_add),
        image = {
            ResourceIcon(
                resourceId = R.drawable.ic_add,
                tint = AthTheme.colors.dark700,
                modifier = Modifier.size(24.dp)
            )
        },
        onClick = onClick,
        showPlaceholder = false,
    )
}

@Composable
private fun NavigationItem(
    id: Followable.Id,
    title: String,
    imageUrl: String,
    background: NavItemBackground,
    showPlaceholder: Boolean,
    @DrawableRes placeholder: Int,
    onClick: (Followable.Id) -> Unit = {},
) {

    val modifier = if (background is NavItemBackground.Empty) {
        Modifier.fillMaxSize()
    } else {
        Modifier.size(24.dp)
    }

    NavItem(
        text = title,
        backgroundColor = background.color,
        image = {
            RemoteImageAsync(
                url = imageUrl,
                circular = background.isCircular,
                modifier = modifier,
                placeholder = if (placeholder != NavigationItem.NO_PLACEHOLDER) placeholder else null
            )
        },
        showPlaceholder = showPlaceholder,
        onClick = { if (showPlaceholder.not()) onClick(id) },
    )
}

@Composable
private fun NavItem(
    text: String,
    image: @Composable () -> Unit,
    backgroundColor: Color = AthTheme.colors.dark300,
    showPlaceholder: Boolean = false,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .width(44.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(44.dp)
                .background(backgroundColor, CircleShape)
                .athPlaceholder(visible = showPlaceholder, shape = CircleShape)
        ) {
            image()
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = text,
            style = AthTextStyle.Calibre.Utility.Regular.Small,
            color = AthTheme.colors.dark700,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier.athPlaceholder(visible = showPlaceholder, shape = RectangleShape)
        )
    }
}

@Preview
@Composable
fun FollowableNavigationBar_Preview() {
    FollowableNavigationBar(
        navItems = listOf(

            SimpleNavItem(
                id = Followable.Id("1", Followable.Type.TEAM),
                title = "CLE",
                background = NavItemBackground.Colored("311D00")
            ),

            SimpleNavItem(
                id = Followable.Id("2", Followable.Type.TEAM),
                title = "GS",
                background = NavItemBackground.Colored("#0C2340")
            ),

            SimpleNavItem(
                id = Followable.Id("2", Followable.Type.LEAGUE),
                title = "NFL"

            ),

            AuthorNavItem(
                SimpleNavItem(
                    id = Followable.Id("10", Followable.Type.AUTHOR),
                    title = "Shams Charania"
                )
            )
        ),
        onEditClick = {},
        onFollowableClick = { _, _ -> },
    )
}