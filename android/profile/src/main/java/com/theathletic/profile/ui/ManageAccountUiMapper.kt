package com.theathletic.profile.ui

import com.theathletic.profile.data.ManageAccountUser

internal fun ManageAccountUser.toUserInformation() = UserInformation(
    currentCustomer = toCustomer(),
    originalCustomer = toCustomer(),
    isFBLinked = isFBLinked,
    isAnonymousAccount = isAnonymousAccount,
    isUserSubscribed = isUserSubscribed,
)

private fun ManageAccountUser.toCustomer() = Customer(
    firstName = firstName.orEmpty(),
    lastName = lastName.orEmpty(),
    email = email.orEmpty(),
)