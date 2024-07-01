package com.theathletic.auth.registration

import com.theathletic.utility.Event

class RegistrationContract {
    enum class Page(val pagerValue: Int) { EMAIL(0), NAME(1) }

    enum class StateType {
        DEFAULT,
        ON_INPUT,

        EMAIL_INVALID_EMAIL,
        EMAIL_INVALID_PASSWORD,
        ERROR_CREATING_ACCOUNT,

        TRANSITION_TO_NAME_PAGE,

        WAITING_FOR_CREATE_ACCOUNT_RESPONSE
    }

    data class State(
        val type: StateType,

        // Email page
        val showInvalidEmailError: Boolean = false,
        val showAccountAlreadyExistsError: Boolean = false,
        val isPromoCheckboxVisible: Boolean = false,
        val isEmailInformationWindowVisible: Boolean = false,
        val showPasswordValidationError: Boolean = false,
        val isNextBtnEnabled: Boolean = false,
        val email: String = "",
        val password: String = "",

        // Name page
        val isCompleteSignupBtnEnabled: Boolean = false,
        val firstName: String = "",
        val lastName: String = "",
        val receivePromos: Boolean = false,

        // Both pages
        val showLoading: Boolean = false,
        val activePage: Page = Page.EMAIL
    ) {
        override fun toString(): String {
            return "State(type=$type, showInvalidEmailError=$showInvalidEmailError, " +
                "showAccountAlreadyExistsError=$showAccountAlreadyExistsError, " +
                "isPromoCheckboxVisible=$isPromoCheckboxVisible, " +
                "isEmailInformationWindowVisible=$isEmailInformationWindowVisible, " +
                "showPasswordValidationError=$showPasswordValidationError, " +
                "isNextBtnEnabled=$isNextBtnEnabled, " +
                "isCompleteSignupBtnEnabled=$isCompleteSignupBtnEnabled, " +
                "receivePromos=$receivePromos, showLoading=$showLoading, " +
                "activePage=$activePage)"
        }
    }

    sealed class Effect : Event() {
        object NetworkError : Effect()

        object ErrorCreatingAccount : Effect()
        object ConditionalScrollToTop : Effect()

        object ContinueAuthFlow : Effect()
    }
}