package com.theathletic.auth.login

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.theathletic.AthleticConfig
import com.theathletic.R
import com.theathletic.auth.AuthFragment
import com.theathletic.auth.authActivity
import com.theathletic.auth.inputText
import com.theathletic.auth.login.LoginContract.Effect
import com.theathletic.auth.login.LoginContract.Effect.AuthError
import com.theathletic.auth.login.LoginContract.Effect.AuthSuccess
import com.theathletic.auth.login.LoginContract.Effect.NetworkError
import com.theathletic.auth.login.LoginContract.Effect.OpenForgotPassword
import com.theathletic.auth.setupToolbar
import com.theathletic.databinding.FragmentLoginBinding
import com.theathletic.extension.getScreenHeight
import com.theathletic.extension.safe
import com.theathletic.extension.visibleIf
import com.theathletic.fragment.AthleticFragment
import com.theathletic.utility.ActivityUtility
import com.theathletic.widget.StatefulLayout
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel
import timber.log.Timber

class LoginFragment :
    AthleticFragment(),
    AuthFragment {
    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var viewModel: LoginViewModel

    private var _binding: FragmentLoginBinding? = null
    private val binding: FragmentLoginBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            setup()
            viewModel.state.collect { state ->
                Timber.i("emitted state: $state")
                render(state)
            }
        }
        viewModel.observe<Effect>(this) { effect ->
            Timber.i("emitted effect: $effect")
            when (effect) {
                AuthSuccess -> authActivity().continueFromAuthentication(shouldShowOnboarding = false)
                is AuthError -> handleAuthError(effect.isInvalidLogin)
                NetworkError -> {
                    binding.loginStatefulLayout.state = StatefulLayout.CONTENT
                    showSnackbar(R.string.global_network_offline)
                }
                OpenForgotPassword -> ActivityUtility.startCustomTabsActivity(
                    context,
                    AthleticConfig.FORGOT_PASSWORD_URL
                )
            }.safe
        }
    }

    private fun render(state: LoginContract.State) {
        binding.buttonLogin.visibleIf(true)
        binding.buttonLogin.isEnabled = state.isLoginBtnEnabled
        binding.inputEmail.error = if (state.showEmailError) {
            getString(R.string.login_email_is_not_valid)
        } else {
            ""
        }
        binding.loginStatefulLayout.state = if (state.showLoader) {
            StatefulLayout.PROGRESS
        } else {
            StatefulLayout.CONTENT
        }
    }

    private suspend fun setup() {
        val state = viewModel.state.first()
        binding.inputEmail.editText?.setText(state.email)
        binding.inputPassword.editText?.setText(state.password)
        setupToolbar(binding.appbarContainer.toolbar, getString(R.string.auth_options_login_title))
        authActivity().supportActionBar?.title = ""
        binding.forgotPassword.setOnClickListener { viewModel.onTapForgotPassword() }
        binding.buttonLogin.setOnClickListener {
            viewModel.sendLoginRequest(
                binding.inputEmail.inputText(),
                binding.inputPassword.inputText()
            )
        }
        binding.inputEmail.editText?.addTextChangedListener(
            afterTextChanged = { text ->
                viewModel.onInput(text.toString(), binding.inputPassword.inputText())
            }
        )
        binding.inputPassword.editText?.addTextChangedListener(
            afterTextChanged = { text ->
                viewModel.onInput(binding.inputEmail.inputText(), text.toString())
            }
        )
        binding.inputPassword.editText?.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                viewModel.onEmailFieldLosesFocus(binding.inputEmail.inputText())
                scrollToPasswordOnSmallDevices()
            }
        }
    }

    private fun scrollToPasswordOnSmallDevices() {
        if (authActivity().getScreenHeight() < 1000) {
            val targetY = binding.inputPassword.y.toInt()
            binding.loginScrollContainer?.scrollTo(0, targetY)
        }
    }

    private fun handleAuthError(isInvalidLogin: Boolean) {
        activity?.let {
            val dialog = AlertDialog.Builder(it).create()
            val view = LayoutInflater.from(it).inflate(R.layout.dialog_auth_error, null)
            view.findViewById<View>(R.id.try_again).setOnClickListener { dialog.dismiss() }
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            val errorMessage = if (isInvalidLogin) {
                R.string.login_email_pass_incorrect
            } else {
                R.string.login_error_generic_message
            }
            view.findViewById<TextView>(R.id.dialog_body).setText(errorMessage)

            dialog.setView(view)
            dialog.show()
        }
    }

    override fun onBackPressed(): Boolean {
        if (authActivity().popBackFragment()) {
            return true
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}