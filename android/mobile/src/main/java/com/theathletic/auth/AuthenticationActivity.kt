package com.theathletic.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.theathletic.R
import com.theathletic.activity.BaseActivity
import com.theathletic.analytics.AnalyticsTracker
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.auth.AuthenticationActivity.FragmentType.AUTHENTICATION
import com.theathletic.auth.AuthenticationActivity.FragmentType.LOGIN
import com.theathletic.auth.AuthenticationActivity.FragmentType.LOGIN_OPTIONS
import com.theathletic.auth.AuthenticationActivity.FragmentType.REGISTRATION
import com.theathletic.auth.AuthenticationActivity.FragmentType.REGISTRATION_OPTIONS
import com.theathletic.auth.login.LoginFragment
import com.theathletic.auth.loginoptions.LoginOptionsFragment
import com.theathletic.auth.registration.RegistrationFragment
import com.theathletic.auth.registrationoptions.RegistrationOptionsFragment
import com.theathletic.utility.ActivityUtility
import org.koin.android.ext.android.inject
import timber.log.Timber

class AuthenticationActivity : BaseActivity() {
    private var currentFragmentType: FragmentType? = null
    private val analytics by inject<Analytics>()
    private val analyticsTracker by inject<AnalyticsTracker>()

    enum class FragmentType {
        AUTHENTICATION,
        LOGIN,
        REGISTRATION,
        LOGIN_OPTIONS,
        REGISTRATION_OPTIONS
    }

    companion object {
        const val EXTRA_FRAGMENT_TYPE = "fragment_type"
        const val EXTRA_IS_POST_PURCHASE = "is_post_purchase"
        const val AUTH_RESULT_CODE = 3213
        private const val EXTRA_FINISH_ON_CONTINUE = "finish_on_continue"

        fun newIntent(
            context: Context,
            fragmentType: FragmentType,
            finishOnContinue: Boolean = false,
            isPostPurchase: Boolean = false
        ) = Intent(context, AuthenticationActivity::class.java).apply {
            putExtra(EXTRA_FRAGMENT_TYPE, fragmentType)
            putExtra(EXTRA_FINISH_ON_CONTINUE, finishOnContinue)
            putExtra(EXTRA_IS_POST_PURCHASE, isPostPurchase)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.data?.encodedAuthority == "oauth-callback") {
            supportFragmentManager.fragments.lastOrNull()?.let { currentFragment ->
                if (currentFragment is OAuthFragment) {
                    currentFragment.processOAuthCallback(intent.dataString!!)
                }
            }
        }
        Timber.i("received new intent: $intent")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        if (savedInstanceState == null) {
            val fragmentType = intent?.extras?.getSerializable(EXTRA_FRAGMENT_TYPE) as FragmentType
            getFragmentByType(fragmentType).let { fragment ->
                supportFragmentManager.beginTransaction().add(R.id.fragment_container, fragment).commit()
                currentFragmentType = fragmentType
            }
        }
    }

    override fun onPause() {
        super.onPause()
        analyticsTracker.startOneOffUploadWork()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> false
        }
    }

    override fun getStatusBarColor() = R.color.ath_grey_65

    override fun setOverrideTransition() {
        // no-op: accept defaults
    }

    fun switchToFragment(fragmentType: FragmentType) {
        val newFragment = getFragmentByType(fragmentType)
        val transaction = supportFragmentManager.beginTransaction()

        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
        transaction.add(R.id.fragment_container, newFragment)
        transaction.addToBackStack(fragmentType.name)
        transaction.commit()
        currentFragmentType = fragmentType
    }

    fun popBackFragment(): Boolean {
        if (supportFragmentManager.backStackEntryCount >= 1) {
            supportFragmentManager.popBackStack()
            return true
        }
        return false
    }

    fun continueFromAuthentication(shouldShowOnboarding: Boolean) {
        if (intent.getBooleanExtra(EXTRA_FINISH_ON_CONTINUE, false)) {
            setResult(Activity.RESULT_OK, Intent())
            finish()
            return
        }

        // Tt Override default close transition
        overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out)

        if (shouldShowOnboarding) {
            ActivityUtility.startOnBoardingActivity(this)
        } else {
            ActivityUtility.startMainActivityNewTask(this)
            finishAffinity()
            if (currentFragmentType == REGISTRATION) {
                analytics.track(Event.Onboarding.Finished)
            }
        }
    }

    private fun getFragmentByType(fragmentType: FragmentType): androidx.fragment.app.Fragment {
        return when (fragmentType) {
            AUTHENTICATION -> AuthenticationFragment.newInstance()
            LOGIN -> LoginFragment.newInstance()
            REGISTRATION -> RegistrationFragment.newInstance()
            LOGIN_OPTIONS -> LoginOptionsFragment.newInstance()
            REGISTRATION_OPTIONS -> RegistrationOptionsFragment.newInstance()
        }
    }
}