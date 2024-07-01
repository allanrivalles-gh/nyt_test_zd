package com.theathletic.profile.ui

data class ManageAccountUiModel(
    val userInformation: UserInformation? = null,
    val isMonthlySubscriber: Boolean = false,
)

data class UserInformation(
    val currentCustomer: Customer,
    val originalCustomer: Customer,
    val isFBLinked: Boolean,
    val isAnonymousAccount: Boolean,
    val isUserSubscribed: Boolean,
) {
    val valuesChanged: Boolean
        get() = currentCustomer.email != originalCustomer.email ||
            currentCustomer.firstName != originalCustomer.firstName ||
            currentCustomer.lastName != originalCustomer.lastName
}

data class Customer(
    val firstName: String,
    val lastName: String,
    val email: String,
)