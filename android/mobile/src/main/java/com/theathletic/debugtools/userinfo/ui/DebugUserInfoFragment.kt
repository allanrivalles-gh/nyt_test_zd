package com.theathletic.debugtools.userinfo.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope
import com.theathletic.debugtools.ui.userinfo.DebugUserInfoScreen
import com.theathletic.fragment.AthleticComposeFragment
import com.theathletic.utility.coroutines.collectIn
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class DebugUserInfoFragment : AthleticComposeFragment<
    DebugUserInfoViewModel,
    DebugUserInfoContract.ViewState
    >() {

    companion object {
        fun newInstance() = DebugUserInfoFragment()
    }

    override fun setupViewModel() = getViewModel<DebugUserInfoViewModel> {
        parametersOf(navigator)
    }

    override fun onResume() {
        super.onResume()
        viewModel.eventConsumer.collectIn(lifecycleScope) { event ->
            when (event) {
                is DebugUserInfoContract.Event.CopyToClipboard -> copyToClipboard(event.key, event.contents)
            }
        }
    }

    private fun copyToClipboard(key: String, contents: String) {
        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE)
            as? ClipboardManager ?: return

        val clip = ClipData.newPlainText("", contents)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(requireContext(), "$key copied", Toast.LENGTH_SHORT).show()
    }

    @Composable
    override fun Compose(state: DebugUserInfoContract.ViewState) {
        DebugUserInfoScreen(
            infoList = state.userInfoList,
            interactor = viewModel
        )
    }
}