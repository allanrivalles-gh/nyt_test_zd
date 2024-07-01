package com.theathletic.auth.loginoptions

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.theathletic.R
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.auth.AuthenticationActivity.FragmentType
import com.theathletic.auth.OAuthFlow
import com.theathletic.auth.OAuthFragment
import com.theathletic.auth.authActivity
import com.theathletic.auth.launchOAuthCustomTab
import com.theathletic.auth.loginoptions.LoginOptionsContract.StateType.INITIAL
import com.theathletic.auth.loginoptions.LoginOptionsContract.StateType.LAUNCH_OAUTH_FLOW
import com.theathletic.auth.loginoptions.LoginOptionsContract.StateType.LOADING_ATHLETIC_LOGIN_CALL
import com.theathletic.auth.loginoptions.LoginOptionsContract.StateType.LOADING_OAUTH_FLOW
import com.theathletic.auth.loginoptions.LoginOptionsContract.StateType.LOGIN_ERROR
import com.theathletic.auth.loginoptions.LoginOptionsContract.StateType.LOGIN_SUCCESS
import com.theathletic.auth.loginoptions.LoginOptionsContract.StateType.OAUTH_FLOW_ERROR
import com.theathletic.auth.setupToolbar
import com.theathletic.databinding.FragmentLoginOptionsBinding
import com.theathletic.extension.extGetString
import com.theathletic.extension.getColorFromAttr
import com.theathletic.extension.safe
import com.theathletic.fragment.AthleticFragment
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.getViewModel
import timber.log.Timber

/**
 * View with three oauth options (apple, facebook, google) or normal email login.
 */
class LoginOptionsFragment : AthleticFragment(), OAuthFragment {
    companion object {
        fun newInstance() = LoginOptionsFragment()
    }

    private lateinit var viewModel: LoginOptionsViewModel
    private var _binding: FragmentLoginOptionsBinding? = null
    private val binding: FragmentLoginOptionsBinding get() = _binding!!

    private val analytics by inject<Analytics>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = getViewModel()
        setupToolbar(binding.appbarContainer.toolbar, getString(R.string.auth_options_login_title))
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                Timber.i("state emitted: $state")
                when (state.type) {
                    INITIAL -> initialState()
                    LOADING_OAUTH_FLOW -> loadingState(state)
                    LAUNCH_OAUTH_FLOW -> launchOAuthCustomTab(state.oAuthUrl!!.url)
                    OAUTH_FLOW_ERROR -> errorState(state)
                    LOADING_ATHLETIC_LOGIN_CALL -> loadingState(state)
                    LOGIN_SUCCESS -> authActivity().continueFromAuthentication(false)
                    LOGIN_ERROR -> errorState(state)
                }.safe
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    override fun processOAuthCallback(oAuthResult: String) {
        viewModel.login(oAuthResult)
    }

    private fun initialState() {
        binding.appleBtn.setOnClickListener {
            analytics.track(Event.Authentication.ClickSignInPage(element = OAuthFlow.APPLE.analyticsName))
            viewModel.onStartOAuthFlow(OAuthFlow.APPLE)
        }
        binding.fbBtn.setOnClickListener {
            analytics.track(Event.Authentication.ClickSignInPage(element = OAuthFlow.FACEBOOK.analyticsName))
            viewModel.onStartOAuthFlow(OAuthFlow.FACEBOOK)
        }
        binding.googleBtn.setOnClickListener {
            analytics.track(Event.Authentication.ClickSignInPage(element = OAuthFlow.GOOGLE.analyticsName))
            viewModel.onStartOAuthFlow(OAuthFlow.GOOGLE)
        }
        binding.nytBtn.apply {
            setOnClickListener {
                analytics.track(Event.Authentication.ClickSignInPage(element = OAuthFlow.NYT.analyticsName))
                viewModel.onStartOAuthFlow(OAuthFlow.NYT)
            }
        }
        binding.emailBtn.setOnClickListener {
            analytics.track(Event.Authentication.ClickSignInPage(element = "email"))
            authActivity().switchToFragment(FragmentType.LOGIN)
        }
        resetButtons()

        setupSignUpLink()
    }

    private fun setupSignUpLink() {
        if (viewModel.showSignUp()) {
            binding.signUp.setOnClickListener {
                analytics.track(Event.Authentication.ClickSignInPage(element = "sign_up"))
                authActivity().switchToFragment(FragmentType.REGISTRATION_OPTIONS)
            }
            binding.signUp.visibility = View.VISIBLE
            binding.dontHaveAccount.visibility = View.VISIBLE
        } else {
            binding.signUp.visibility = View.INVISIBLE
            binding.dontHaveAccount.visibility = View.INVISIBLE
        }
    }

    private fun loadingState(state: LoginOptionsContract.State) {
        val loadingColor = activity?.getColorFromAttr(R.attr.colorOnSurface) ?: Color.WHITE
        when (state.activeAuthFlow) {
            OAuthFlow.FACEBOOK -> {
                bindProgressButton(binding.fbBtn)
                binding.fbBtn.showProgress { progressColor = loadingColor }
                disableButtons()
            }
            OAuthFlow.GOOGLE -> {
                bindProgressButton(binding.googleBtn)
                binding.googleBtn.showProgress { progressColor = loadingColor }
                disableButtons()
            }
            OAuthFlow.APPLE -> {
                bindProgressButton(binding.appleBtn)
                binding.appleBtn.showProgress { progressColor = loadingColor }
                disableButtons()
            }
            OAuthFlow.NYT -> {
                bindProgressButton(binding.nytBtn)
                binding.nytBtn.showProgress { progressColor = loadingColor }
                disableButtons()
            }
            else -> {}
        }
    }

    private fun errorState(state: LoginOptionsContract.State) {
        resetButtons()
        showSnackbar(state.errorMessage.extGetString())
    }

    private fun disableButtons() {
        binding.fbBtn.isEnabled = false
        binding.appleBtn.isEnabled = false
        binding.googleBtn.isEnabled = false
        binding.nytBtn.isEnabled = false
        binding.emailBtn.isEnabled = false
    }

    private fun resetButtons() {
        binding.fbBtn.hideProgress(resources.getString(R.string.auth_options_continue_fb))
        binding.fbBtn.isEnabled = true
        binding.googleBtn.hideProgress(resources.getString(R.string.auth_options_continue_google))
        binding.googleBtn.isEnabled = true
        binding.appleBtn.hideProgress(resources.getString(R.string.auth_options_continue_apple))
        binding.appleBtn.isEnabled = true
        binding.nytBtn.hideProgress(resources.getString(R.string.auth_options_continue_nyt))
        binding.nytBtn.isEnabled = true
        binding.emailBtn.isEnabled = true
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}