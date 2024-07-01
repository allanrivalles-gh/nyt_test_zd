package com.theathletic.main.ui

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import com.theathletic.ui.UiModel
import com.theathletic.ui.binding.ParameterizedString

/**
 * Models the display for SecondaryNavigationItems in a given PrimaryNavigationItem.
 */
sealed class SecondaryNavigationItem(
    primaryNavigationItem: PrimaryNavigationItem,
    stableIdSuffix: String,
    val showLiveIndicator: Boolean = false,
) : UiModel {

    override val stableId: String = "${primaryNavigationItem.title}:$stableIdSuffix"

    class StringBased(
        primaryNavigationItem: PrimaryNavigationItem,
        uniqueId: String,
        val title: String
    ) : SecondaryNavigationItem(primaryNavigationItem, uniqueId)

    class ParameterizedStringBased(
        primaryNavigationItem: PrimaryNavigationItem,
        val parameterizedString: ParameterizedString
    ) : SecondaryNavigationItem(primaryNavigationItem, parameterizedString.hashCode().toString())

    class ResourceBased(
        primaryNavigationItem: PrimaryNavigationItem,
        @StringRes val title: Int,
        showLiveIndicator: Boolean = false,
    ) : SecondaryNavigationItem(primaryNavigationItem, title.toString(), showLiveIndicator)

    class SingleWithoutNavigation(
        primaryNavigationItem: PrimaryNavigationItem
    ) : SecondaryNavigationItem(primaryNavigationItem, "SINGLE")

    /**
     * Item used for the Feed module's initial button. This button has special behavior. When
     * clicked from another tab, we navigate to it normally. But if we click on it while it is
     * selected, we bring up the search list to see a one-off feed and can follow it.
     */
    class DefaultBrowsingItem(
        primaryNavigationItem: PrimaryNavigationItem,
        val title: String
    ) : SecondaryNavigationItem(primaryNavigationItem, title)

    class DefaultBrowsingResourceBaseItem(
        primaryNavigationItem: PrimaryNavigationItem,
        @StringRes val title: Int
    ) : SecondaryNavigationItem(primaryNavigationItem, title.toString())

    /**
     * This item represents the temporary secondary item that is inserted after selecting a topic
     * with the "More" button. It acts differently than other items because it disappears and is
     * replaced with the "More" item when navigating away from it.
     */
    class BrowsingItem(
        primaryNavigationItem: PrimaryNavigationItem,
        val title: String
    ) : SecondaryNavigationItem(primaryNavigationItem, "BROWSE:$title")
}

@Composable
fun SecondaryNavigationItem.titleAsString(context: Context): String {
    return when (this) {
        is SecondaryNavigationItem.StringBased -> title
        is SecondaryNavigationItem.ParameterizedStringBased -> {
            val pString = parameterizedString
            context.getString(pString.stringRes, *pString.parameters.toTypedArray())
        }
        is SecondaryNavigationItem.ResourceBased -> context.getString(title)
        is SecondaryNavigationItem.SingleWithoutNavigation -> ""
        is SecondaryNavigationItem.DefaultBrowsingItem -> title
        is SecondaryNavigationItem.DefaultBrowsingResourceBaseItem -> context.getString(title)
        is SecondaryNavigationItem.BrowsingItem -> title
    }
}