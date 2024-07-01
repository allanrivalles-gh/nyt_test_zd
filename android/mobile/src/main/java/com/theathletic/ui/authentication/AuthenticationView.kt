package com.theathletic.ui.authentication

import com.theathletic.ui.BaseView

interface AuthenticationView : BaseView {
    fun onLoginClick()
    fun onGetStartedClick()
    fun openDebugToolsClick()
}