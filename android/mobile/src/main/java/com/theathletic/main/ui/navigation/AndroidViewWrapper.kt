package com.theathletic.main.ui.navigation

import android.content.Context
import android.util.SparseArray
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.fragment.app.findFragment

/**
 * Based on Google's AndroidViewBinding from androidx.compose.ui.viewinterop package
 * it solves two problems of the original implementation:
 * - it does not restore the state of a tab / fragment, meaning each fragment is reloaded every time
 * - it does not load secondary fragment items properly
 */
@Composable
fun AndroidViewWrapper(
    uniqueId: Int,
    tabState: TabState,
    modifier: Modifier = Modifier,
    commit: FragmentTransaction.(containerId: Int) -> Unit
) {
    val localView = LocalView.current
    val localContext = LocalContext.current
    val localData = LocalData(localView, localContext)

    val parentFragment = remember(localView) {
        try {
            localView.findFragment<Fragment>()
        } catch (e: IllegalStateException) {
            null
        }
    }
    val container = remember { mutableStateOf<FragmentContainerView?>(null) }
    val viewBlock: (Context) -> View = remember(localView) {
        { context ->
            FragmentContainerView(context)
                .apply { id = uniqueId }
                .also {
                    val fragmentManager = getFragmentManager(parentFragment, context as FragmentActivity)
                    fragmentManager?.commit(allowStateLoss = true) { commit(it.id) }
                    container.value = it
                }
        }
    }
    AndroidView(modifier = modifier, factory = viewBlock)
    OnDispose(localData, container, parentFragment, uniqueId, tabState)
}

// We verify if the fragment is not composed anymore and remove it
@Composable
private fun OnDispose(
    localData: LocalData,
    container: MutableState<FragmentContainerView?>,
    parentFragment: Fragment?,
    uniqueId: Int,
    tabState: TabState
) {
    val (localView, localContext) = localData
    DisposableEffect(localView, localContext, container) {
        onDispose {
            val fragmentManager = getFragmentManager(parentFragment, localContext as FragmentActivity)
            val existingFragment = fragmentManager?.findFragmentById(container.value?.id ?: 0)
            existingFragment?.let {
                if (fragmentManager.isStateSaved.not()) {
                    tabState.savedStates.put(uniqueId, fragmentManager.saveFragmentInstanceState(existingFragment))
                    fragmentManager.commit(allowStateLoss = true) { remove(existingFragment) }
                }
            }
        }
    }
}

fun getFragmentManager(
    parentFragment: Fragment?,
    fragmentActivity: FragmentActivity?
): FragmentManager? {
    return parentFragment?.childFragmentManager ?: fragmentActivity?.supportFragmentManager
}

data class FragmentData(
    val uniqueId: Int,
    val fragment: Fragment,
    val fragmentManager: FragmentManager
)

data class TabState(
    val savedStates: SparseArray<Fragment.SavedState>,
    val selectedTabId: MutableState<Int>
)

private typealias LocalData = Pair<View, Context>