package com.theathletic.feed.ui.modules.audio

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.theathletic.entity.main.PodcastTopicEntryType
import com.theathletic.feed.ui.FeedInteraction
import com.theathletic.feed.ui.FeedModuleV2
import com.theathletic.feed.ui.LocalFeedInteractor
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.ui.R
import com.theathletic.ui.widgets.RemoteImage
import com.theathletic.ui.widgets.ResourceIcon

data class PodcastCategoriesModule(
    val id: String,
    val categories: List<Category>,
) : FeedModuleV2 {

    override val moduleId: String
        get() = "PodcastCategoriesModule-$id"

    data class Category(
        val id: String,
        val type: PodcastTopicEntryType,
        val name: String,
        val imageUrl: String,
        val payload: Payload = Payload(),
    ) {
        data class Payload(
            val categoryType: String = "",
            val moduleIndex: Int = -1,
            val vIndex: Int = -1,
        )
    }

    @Composable
    override fun Render() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(AthTheme.colors.dark200)
                .padding(top = 24.dp, bottom = 16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.podcast_feed_browse_by_category),
                color = AthTheme.colors.dark800,
                style = AthTextStyle.Slab.Bold.Small,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                categories.forEachIndexed { index, category ->
                    CategoryRow(category)
                    if (index != categories.indices.last) {
                        Divider(color = AthTheme.colors.dark300)
                    }
                }
            }
        }
    }

    sealed class Interaction : FeedInteraction {
        data class CategoryClick(
            val id: String,
            val type: PodcastTopicEntryType,
            val name: String,
            val payload: Category.Payload,
        ) : Interaction()
    }
}

@Composable
private fun CategoryRow(
    category: PodcastCategoriesModule.Category,
) {
    val interactor = LocalFeedInteractor.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(60.dp)
            .fillMaxWidth()
            .clickable {
                interactor.send(
                    PodcastCategoriesModule.Interaction.CategoryClick(
                        id = category.id,
                        type = category.type,
                        name = category.name,
                        payload = category.payload,
                    )
                )
            },
    ) {
        RemoteImage(
            url = category.imageUrl,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .size(30.dp),
        )

        Text(
            text = category.name,
            color = AthTheme.colors.dark800,
            style = AthTextStyle.Calibre.Utility.Medium.ExtraLarge,
            modifier = Modifier.weight(1f)
        )

        ResourceIcon(
            resourceId = R.drawable.ic_arrow_right,
            modifier = Modifier.padding(end = 24.dp).size(24.dp),
            tint = AthTheme.colors.dark800,
        )
    }
}

@Preview
@Composable
private fun PodcastCategoriesModulePreview() {
    PodcastCategoriesModule(
        id = "1",
        categories = listOf(
            PodcastCategoriesModule.Category(
                id = "1",
                type = PodcastTopicEntryType.LEAGUE,
                name = "NBA",
                imageUrl = "",
            ),
            PodcastCategoriesModule.Category(
                id = "2",
                type = PodcastTopicEntryType.LEAGUE,
                name = "MLB",
                imageUrl = "",
            ),
            PodcastCategoriesModule.Category(
                id = "3",
                type = PodcastTopicEntryType.LEAGUE,
                name = "Soccer",
                imageUrl = "",
            ),
            PodcastCategoriesModule.Category(
                id = "4",
                type = PodcastTopicEntryType.LEAGUE,
                name = "Motorsports",
                imageUrl = "",
            ),
        )
    ).Render()
}