package com.theathletic.auth.registration

import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.PagerAdapter
import com.theathletic.R
import com.theathletic.R.string
import com.theathletic.auth.AuthFragment
import com.theathletic.auth.authActivity
import com.theathletic.auth.registration.RegistrationContract.Effect.ConditionalScrollToTop
import com.theathletic.auth.registration.RegistrationContract.Effect.ContinueAuthFlow
import com.theathletic.auth.registration.RegistrationContract.Effect.ErrorCreatingAccount
import com.theathletic.auth.registration.RegistrationContract.Effect.NetworkError
import com.theathletic.auth.registration.RegistrationContract.Page
import com.theathletic.auth.registration.RegistrationContract.State
import com.theathletic.auth.setupToolbar
import com.theathletic.databinding.FragmentRegistrationBinding
import com.theathletic.databinding.FragmentRegistrationPageEmailBinding
import com.theathletic.databinding.FragmentRegistrationPageNameBinding
import com.theathletic.event.SnackbarEvent
import com.theathletic.extension.extGetString
import com.theathletic.extension.extSetClickableSpanUnderlineBold
import com.theathletic.extension.getScreenHeight
import com.theathletic.extension.visibleIf
import com.theathletic.fragment.AthleticFragment
import com.theathletic.utility.ActivityUtility
import com.theathletic.utility.getPrivacyPolicyLink
import com.theathletic.utility.getTermsOfServiceLink
import com.theathletic.widget.StatefulLayout
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf
import timber.log.Timber

class RegistrationFragment :
    AthleticFragment(),
    AuthFragment {
    companion object {
        fun newInstance() = RegistrationFragment()
    }

    private var adapter: RegistrationPagerAdapter? = null

    private var stateJob: Job? = null

    private lateinit var viewModel: RegistrationViewModel
    private var _binding: FragmentRegistrationBinding? = null
    private val binding: FragmentRegistrationBinding get() = _binding!!
    private var emailPageBinding: FragmentRegistrationPageEmailBinding? = null
    private var registrationPageBinding: FragmentRegistrationPageNameBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = getViewModel { parametersOf(getExtras()) }
        setup()
        viewModel.observe<SnackbarEvent>(viewLifecycleOwner) { showSnackbar(it.message) }
    }

    override fun onBackPressed(): Boolean { return viewModel.onBackPressed() }

    private fun startObserving() {
        if (stateJob != null) return
        stateJob = lifecycleScope.launch {
            viewModel.state
                .collect { state ->
                    Timber.i("emitted state: $state")
                    render(state)
                }
        }
        viewModel.observe<RegistrationContract.Effect>(this) { effect ->
            Timber.i("emitted effect: $effect")
            when (effect) {
                NetworkError -> showSnackbar(string.global_network_offline)
                ErrorCreatingAccount -> {
                    viewModel.onBackPressed()
                }
                ContinueAuthFlow -> authActivity().continueFromAuthentication(shouldShowOnboarding = false)
                ConditionalScrollToTop -> emailPageBinding?.emailScrollContainer?.scrollTo(0, 0)
            }
        }
    }

    private fun render(state: State) {
        binding.registrationStatefulLayout.state = if (state.showLoading) {
            StatefulLayout.PROGRESS
        } else {
            StatefulLayout.CONTENT
        }

        if (binding.viewPager.currentItem != state.activePage.pagerValue) {
            binding.viewPager.setCurrentItem(state.activePage.pagerValue, true)
            setActionBarForPosition(state.activePage.pagerValue)
        }

        if (state.activePage == Page.EMAIL) {
            renderEmailPage(state)
        } else {
            renderNamePage(state)
        }
    }

    private fun renderEmailPage(state: State) {
        emailPageBinding?.inputEmail?.error = if (state.showAccountAlreadyExistsError) {
            getString(R.string.auth_account_already_exists)
        } else if (state.showInvalidEmailError) {
            getString(R.string.login_email_is_not_valid)
        } else {
            ""
        }
        emailPageBinding?.inputPassword?.error = if (state.showPasswordValidationError) {
            getString(R.string.validation_error_range_length, 8, 64)
        } else {
            ""
        }
        emailPageBinding?.buttonNext?.isEnabled = state.isNextBtnEnabled
        emailPageBinding?.buttonNext?.visibleIf(true)
    }

    private fun renderNamePage(state: State) {
        registrationPageBinding?.completeSignup?.isEnabled = state.isCompleteSignupBtnEnabled
    }

    private fun setup() {
        if (adapter == null) {
            adapter = RegistrationPagerAdapter()
        }
        binding.viewPager.adapter = adapter
        binding.registrationStatefulLayout.state = StatefulLayout.CONTENT
        setupToolbar(binding.appbarContainer.toolbar, getString(R.string.registration_create_account_title))
    }

    private fun setupEmailPage(state: State) {
        emailPageBinding?.inputEmail?.editText?.setText(state.email)
        emailPageBinding?.inputPassword?.editText?.setText(state.password)
        emailPageBinding?.inputPassword?.editText?.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                viewModel.onEmailLostFocus()
                scrollToTopOnSmallDevices()
            }
        }
        emailPageBinding?.inputEmail?.editText?.addTextChangedListener(
            afterTextChanged = {
                viewModel.onInput(email = it.toString())
            }
        )
        emailPageBinding?.inputPassword?.editText?.addTextChangedListener(
            afterTextChanged = {
                viewModel.onInput(password = it.toString())
            }
        )
        emailPageBinding?.buttonNext?.setOnClickListener { viewModel.onEmailPageNextClicked() }
        setupTermsLinks()
    }

    private fun scrollToTopOnSmallDevices() {
        if (authActivity().getScreenHeight() < 1000) {
            val targetY = emailPageBinding?.inputPassword?.y?.toInt() ?: 0
            emailPageBinding?.emailScrollContainer?.scrollTo(0, targetY)
        }
    }

    private fun setupNamePage(state: State) {
        registrationPageBinding?.completeSignup?.setOnClickListener {
            viewModel.sendRegistrationRequest()
        }
        registrationPageBinding?.inputFirstName?.editText?.setText(state.firstName)
        registrationPageBinding?.inputLastName?.editText?.setText(state.lastName)
        registrationPageBinding?.inputFirstName?.editText?.addTextChangedListener(
            afterTextChanged = {
                viewModel.onInput(firstName = it.toString())
            }
        )
        registrationPageBinding?.inputLastName?.editText?.addTextChangedListener(
            afterTextChanged = {
                viewModel.onInput(lastName = it.toString())
            }
        )
        registrationPageBinding?.inputLastName?.editText?.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus && emailPageBinding?.emailScrollContainer != null &&
                authActivity().getScreenHeight() < 1000
            ) {
                val targetY = registrationPageBinding?.inputLastName?.y?.toInt() ?: 0
                emailPageBinding?.emailScrollContainer?.scrollTo(0, targetY)
            }
        }
        registrationPageBinding?.inputReceivePromosCheckbox?.visibleIf(state.isPromoCheckboxVisible)
        registrationPageBinding?.inputReceivePromosCheckbox?.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.onInput(receivePromoEmails = isChecked)
        }
    }

    private fun setupTermsLinks() {
        val spannableString = SpannableString(resources.getString(R.string.registration_terms_text))
        val termsString = R.string.registration_terms_terms_span.extGetString()
        val privacyString = R.string.registration_terms_privacy_span.extGetString()
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

        emailPageBinding?.termsText?.text = spannableString
        emailPageBinding?.termsText?.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun setActionBarForPosition(position: Int) {
        authActivity().supportActionBar?.let { actionBar ->
            when (position) {
                Page.EMAIL.pagerValue -> {
                    actionBar.setDisplayHomeAsUpEnabled(false)
                    setupToolbar(binding.appbarContainer.toolbar, getString(R.string.registration_create_account_title))
                }
                Page.NAME.pagerValue -> {
                    actionBar.setDisplayHomeAsUpEnabled(true)
                    setupToolbar(binding.appbarContainer.toolbar, getString(R.string.registration_almost_done_title))
                }
            }
        }
    }

    inner class RegistrationPagerAdapter : PagerAdapter() {
        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object`
        }

        override fun getCount(): Int {
            return 2
        }

        override fun instantiateItem(parent: ViewGroup, position: Int): Any {
            return if (position == Page.EMAIL.pagerValue) {
                emailPageBinding = FragmentRegistrationPageEmailBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                parent.addView(emailPageBinding?.root)
                lifecycleScope.launch { setupEmailPage(viewModel.state.first()) }
                startObserving()
                emailPageBinding!!.root
            } else {
                registrationPageBinding = FragmentRegistrationPageNameBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                parent.addView(registrationPageBinding?.root)
                lifecycleScope.launch { setupNamePage(viewModel.state.first()) }
                startObserving()
                registrationPageBinding!!.root
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        emailPageBinding = null
        registrationPageBinding = null
    }
}