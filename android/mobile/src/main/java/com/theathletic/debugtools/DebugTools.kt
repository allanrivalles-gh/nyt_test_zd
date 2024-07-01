package com.theathletic.debugtools

import android.text.TextWatcher
import androidx.databinding.Observable
import androidx.databinding.ObservableLong
import androidx.lifecycle.MutableLiveData
import com.theathletic.R
import com.theathletic.compass.Experiment
import com.theathletic.extension.ObservableString
import com.theathletic.extension.ObservableStringNonNull
import com.theathletic.extension.extGetColor
import java.text.SimpleDateFormat
import java.util.Locale

data class DebugToolsSectionHeader(val title: String) : DebugToolsBaseItem()

data class DebugToolsSectionSubHeader(val title: String) : DebugToolsBaseItem()

data class DebugToolsCustomButton(
    val title: String,
    val backgroundColor: Int = R.color.gray.extGetColor(),
    val onButtonClick: () -> Unit
) : DebugToolsBaseItem()

data class DebugToolsCompassVariantSelectText(
    val selectedVariant: ObservableStringNonNull,
    val experiment: Experiment
) : DebugToolsBaseItem()

data class DebugToolsCustomSwitch(
    val title: String,
    val state: MutableLiveData<Boolean>,
    val setToOn: () -> Unit,
    val setToOff: () -> Unit
) : DebugToolsBaseItem()

data class DebugToolsBaseUrlOverride(
    val title: String,
    val currentValue: ObservableString,
    val onSetClick: (value: String) -> Unit,
    val onResetClick: () -> Unit
) : DebugToolsBaseItem()

data class DebugToolsSendDeeplink(
    val title: String,
    val deeplinkUrl: ObservableString,
    val onSendClick: (value: String) -> Unit
) : DebugToolsBaseItem()

data class DebugToolsCountdown(
    val title: String,
    val bumpButtonLabel: String,
    val onBumpCountdown: (value: ObservableLong) -> Unit,
    val onResetCountdown: (value: ObservableLong) -> Unit
) : DebugToolsBaseItem() {
    val endTimeAsString = ObservableString()
    val endTime: ObservableLong = ObservableLong().apply {
        addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                endTimeAsString.set(getDisplayString(this@apply.get()))
            }
        })
    }

    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    private fun getDisplayString(time: Long): String {
        return if (time == 0L) "n/a" else simpleDateFormat.format(time)
    }
}

data class DebugToolsTextInput(
    val title: String,
    val input: ObservableString,
    val textChanged: TextWatcher
) : DebugToolsBaseItem()