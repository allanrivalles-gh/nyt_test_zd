package com.theathletic.profile.ui.consent

import androidx.lifecycle.ViewModel
import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.profile.data.remote.TranscendConsentWrapper

class ConsentViewModel @AutoKoin constructor(
    transcendConsentWrapper: TranscendConsentWrapper,
) : ViewModel() {
    val consentUrl = transcendConsentWrapper.transcendConsentUrl
}