package com.theathletic.ui.list

import androidx.annotation.DimenRes
import com.theathletic.ui.UiModel
import java.util.concurrent.atomic.AtomicInteger

/**
 * A special presentation model that enables us to asynchronously load list sections without the
 * list jumping around.
 *
 * The declarative list system allows us to load different sections at different times but if, for
 * example, the second section loads before the first, the list jumps around because it focuses on
 * the second section, then it tries to keep it's place and load the first section above the
 * viewport.
 *
 *
 * Adding this root as the top presentation model to all lists means that nothing will be inserted
 * before it doesn't matter in which order sections load, the list will always start focused on this
 * root model.
 */
object ListRoot : UiModel {
    override val stableId = "ROOT_ID"
}

object ListLoadingItem : UiModel {
    override val stableId = "LOADING"
}

object DefaultEmptyUiModel : UiModel {
    override val stableId = "EMPTY"
}

class ListVerticalPadding(@DimenRes val paddingRes: Int) : UiModel {
    companion object {
        var uniqueId = AtomicInteger(1)
    }
    override val stableId = "PADDING:${uniqueId.incrementAndGet()}"
}