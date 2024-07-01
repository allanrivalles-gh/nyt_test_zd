package com.theathletic.profile

import com.theathletic.annotation.autokoin.AutoKoin
import com.theathletic.profile.data.ConsentRepository

class ShouldShowInitialConsentUseCase @AutoKoin constructor(
    private val consentRepository: ConsentRepository,
) {
    suspend operator fun invoke() = consentRepository.getIsUserInGDPR() &&
        consentRepository.getIsConsentConfirmed().not()
}