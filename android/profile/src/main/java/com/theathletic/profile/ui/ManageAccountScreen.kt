package com.theathletic.profile.ui

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun ManageAccountScreen(viewModel: ManageAccountViewModel) {
    val state by viewModel.viewState.collectAsState()
    when {
        state.isLoading -> { }
        state.isEmpty -> { }
        else -> ManageAccountUi(state.uiModel)
    }
}

@Composable
private fun ManageAccountUi(uiModel: ManageAccountUiModel) {
    Text("Account Screen")
}