package com.theathletic.conduct

import com.theathletic.codeofconduct.ui.CodeOfConductUi

interface CodeOfConductContract {
    interface Interaction :
        com.theathletic.presenter.Interactor,
        CodeOfConductUi.Interactor

    data class ViewState(
        val codeOfConductUi: CodeOfConductUi
    ) : com.theathletic.ui.ViewState
}