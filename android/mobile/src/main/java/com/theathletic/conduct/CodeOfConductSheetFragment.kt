package com.theathletic.conduct

import androidx.compose.runtime.Composable
import com.theathletic.codeofconduct.ui.CodeOfConductScreen
import com.theathletic.fragment.AthleticComposeFragment
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class CodeOfConductSheetFragment :
    AthleticComposeFragment<CodeOfConductSheetViewModel, CodeOfConductContract.ViewState>() {
    override fun setupViewModel() = getViewModel<CodeOfConductSheetViewModel> {
        parametersOf(navigator)
    }

    @Composable
    override fun Compose(state: CodeOfConductContract.ViewState) {
        CodeOfConductScreen(
            codeOfConductUi = state.codeOfConductUi,
            onFAQClicked = viewModel::onFAQClicked,
            onContactSupportClicked = viewModel::onContactSupportClicked,
            onAgreeClicked = viewModel::onAgreeClicked,
            onDisagreeClicked = viewModel::onDisagreeClicked,
        )
    }
}