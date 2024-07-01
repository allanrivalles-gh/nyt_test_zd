package com.theathletic.preferences.ui

interface IPreferenceToggleView {
    fun onPreferenceToggled(item: PreferenceSwitchItem, isOn: Boolean)
}