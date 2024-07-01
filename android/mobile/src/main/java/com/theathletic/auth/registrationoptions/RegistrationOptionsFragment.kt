package com.theathletic.auth.registrationoptions

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.theathletic.R
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.auth.AuthenticationActivity
import com.theathletic.auth.AuthenticationActivity.FragmentType
import com.theathletic.auth.OAuthFlow
import com.theathletic.auth.OAuthFragment
import com.theathletic.auth.authActivity
import com.theathletic.auth.launchOAuthCustomTab
import com.theathletic.auth.registrationoptions.RegistrationOptionsContract.StateType.INITIAL
import com.theathletic.auth.registrationoptions.RegistrationOptionsContract.StateType.LAUNCH_OAUTH_FLOW
import com.theathletic.auth.registrationoptions.RegistrationOptionsContract.StateType.LOADING_ATHLETIC_SIGNUP_CALL
import com.theathletic.auth.registrationoptions.RegistrationOptionsContract.StateType.LOADING_OAUTH_FLOW
import com.theathletic.auth.registrationoptions.RegistrationOptionsContract.StateType.OAUTH_FLOW_ERROR
import com.theathletic.auth.registrationoptions.RegistrationOptionsContract.StateType.SIGNUP_ERROR
import com.theathletic.auth.registrationoptions.RegistrationOptionsContract.StateType.SIGNUP_SUCCESS
import com.theathletic.auth.setupToolbar
import com.theathletic.databinding.FragmentRegistrationOptionsBinding
import com.theathletic.extension.extGetString
import com.theathletic.extension.extSetClickableSpanUnderlineBold
import com.theathletic.extension.safe
import com.theathletic.fragment.AthleticFragment
import com.theathletic.utility.ActivityUtility
import com.theathletic.utility.getPrivacyPolicyLink
import com.theathletic.utility.getTermsOfServiceLink
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.getViewModel
import timber.log.Timber

/**
 * View with two oauth options (facebook, google) or normal email registration.
 */
class RegistrationOptionsFragment : AthleticFragment(), OAuthFragment {
    companion object {
        fun newInstance() = RegistrationOptionsFragment()
    }

    private lateinit var viewModel: RegistrationOptionsViewModel
    private var _binding: FragmentRegistrationOptionsBinding? = null
    private val binding: FragmentRegistrationOptionsBinding get() = _binding!!

    private val analytics by inject<Analytics>()
    private var isPostPurchase = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (getExtras()?.getBoolean(AuthenticationActivity.EXTRA_IS_POST_PURCHASE) == true) {
            isPostPurchase = true
            setupToolbar(binding.appbarContainer.toolbar, getString(R.string.auth_options_signup_purchase_title))
            (activity as? AppCompatActivity)?.supportActionBar?.apply {
                setDisplayShowHomeEnabled(false)
                setDisplayHomeAsUpEnabled(false)
                setHomeButtonEnabled(false)
            }
            binding.subHeadline.text = getString(R.string.auth_purchase_sub_headline)
        } else {
            isPostPurchase = false
            setupToolbar(binding.appbarContainer.toolbar, getString(R.string.auth_options_signup_title))
            binding.subHeadline.text = getString(R.string.auth_sub_headline)
        }

        viewModel = getViewModel()
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                Timber.d("state emitted: $state")
                when (state.type) {
                    INITIAL -> initialState()
                    LOADING_OAUTH_FLOW -> loadingState(state)
                    LAUNCH_OAUTH_FLOW -> launchOAuthCustomTab(state.oAuthUrl!!.url)
                    OAUTH_FLOW_ERROR -> errorState(state)
                    LOADING_ATHLETIC_SIGNUP_CALL -> loadingState(state)
                    SIGNUP_SUCCESS -> authActivity().continueFromAuthentication(false)
                    SIGNUP_ERROR -> errorState(state)
                }.safe
            }
        }
        setupTermsLinks()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun processOAuthCallback(oAuthResult: String) {
        viewModel.signup(oAuthResult)
    }

    override fun onBackPressed(): Boolean {
        return if (isPostPurchase) {
            true
        } else {
            super.onBackPressed()
        }
    }

    private fun initialState() {
        resetButtons()
        binding.googleBtn.setOnClickListener {
            analytics.track(Event.Authentication.ClickSignUpPage(element = "google"))
            viewModel.onStartOAuthFlow(OAuthFlow.GOOGLE)
        }
        binding.fbBtn.setOnClickListener {
            analytics.track(Event.Authentication.ClickSignUpPage(element = "facebook"))
            viewModel.onStartOAuthFlow(OAuthFlow.FACEBOOK)
        }
        binding.emailBtn.setOnClickListener {
            analytics.track(Event.Authentication.ClickSignUpPage(element = "email"))
            authActivity().switchToFragment(FragmentType.REGISTRATION)
        }

        setupSignInLink()
    }

    private fun setupSignInLink() {
        binding.logIn.apply {
            text = getString(
                if (isPostPurchase) R.string.auth_link_account else R.string.auth_options_login_title
            )
            setOnClickListener {
                val signUpText = if (isPostPurchase) "link_account" else "login"
                analytics.track(Event.Authentication.ClickSignUpPage(element = signUpText))
                authActivity().switchToFragment(FragmentType.LOGIN_OPTIONS)
            }
            visibility = View.VISIBLE
        }
        binding.subHeadline.visibility = View.VISIBLE
        binding.alreadyHaveAccount.visibility = View.VISIBLE
    }

    private fun loadingState(state: RegistrationOptionsContract.State) {
        when (state.activeAuthFlow) {
            OAuthFlow.FACEBOOK -> {
                bindProgressButton(binding.fbBtn)
                binding.fbBtn.showProgress { progressColor = Color.WHITE }
                disableButtons()
            }
            OAuthFlow.GOOGLE -> {
                bindProgressButton(binding.googleBtn)
                binding.googleBtn.showProgress { progressColor = Color.WHITE }
                disableButtons()
            }
            else -> {}
        }
    }

    private fun errorState(state: RegistrationOptionsContract.State) {
        resetButtons()
        showSnackbar(state.errorMessage.extGetString())
    }

    private fun disableButtons() {
        binding.fbBtn.isEnabled = false
        binding.googleBtn.isEnabled = false
        binding.emailBtn.isEnabled = false
    }

    private fun resetButtons() {
        binding.fbBtn.hideProgress(R.string.auth_options_continue_fb.extGetString())
        binding.fbBtn.isEnabled = true
        binding.googleBtn.hideProgress(R.string.auth_options_continue_google.extGetString())
        binding.googleBtn.isEnabled = true
        binding.emailBtn.isEnabled = true
    }

    private fun setupTermsLinks() {
        val spannableString = SpannableString(resources.getString(R.string.registration_terms_text))
        val termsString = resources.getString(R.string.registration_terms_terms_span)
        val privacyString = resources.getString(R.string.registration_terms_privacy_span)
        val termsClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                ActivityUtility.startCustomTabsActivity(context, getTermsOfServiceLink())
            }
        }

        val privacyClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                ActivityUtility.startCustomTabsActivity(context, getPrivacyPolicyLink())
            }
        }

        spannableString.extSetClickableSpanUnderlineBold(termsString, termsClickableSpan)
        spannableString.extSetClickableSpanUnderlineBold(privacyString, privacyClickableSpan)

        view?.findViewById<TextView>(R.id.terms_text)?.apply {
            text = spannableString
            movementMethod = LinkMovementMethod.getInstance()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}