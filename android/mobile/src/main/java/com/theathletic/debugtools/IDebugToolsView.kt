package com.theathletic.debugtools

import android.view.View
import androidx.databinding.ObservableLong
import com.theathletic.extension.ObservableString
import com.theathletic.ui.BaseView
import org.alfonz.adapter.AdapterView

interface IDebugToolsView : BaseView, AdapterView {
    fun onFeatureSwitchChange(entryKey: String)
    fun onCompassVariantTextClick(view: View, data: DebugToolsCompassVariantSelectText)
    fun onCustomButtonClick(onButtonClick: () -> Unit)
    fun onCustomSwitchChangedClick(
        customSwitch: View,
        switchedOn: () -> Unit,
        switchedOff: () -> Unit
    )
    fun onSetClick(onSetClick: (value: String) -> Unit, currentValue: ObservableString)
    fun onResetClick(onResetClick: () -> Unit, currentValue: ObservableString)
    fun onSendLink(onSendLink: (value: String?) -> Unit, deeplinkUrl: ObservableString)
    fun onBumpCountdownClick(
        onBumpCountdown: (value: ObservableLong) -> Unit,
        currentValue: ObservableLong
    )
    fun onResetCountdownClick(
        onResetCountdown: (value: ObservableLong) -> Unit,
        currentValue: ObservableLong
    )
}