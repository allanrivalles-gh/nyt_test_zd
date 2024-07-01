package com.theathletic.subscriptionplans

import android.content.Intent
import android.view.LayoutInflater
import androidx.databinding.ViewDataBinding
import com.theathletic.attributionsurvey.ui.SurveyActivity
import com.theathletic.auth.AuthenticationActivity
import com.theathletic.databinding.FragmentSubscriptionPlansBinding
import com.theathletic.databinding.FragmentSubscriptionPlansSpecialOfferBinding
import com.theathletic.fragment.AthleticMvpBindingFragment
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class SubscriptionPlansFragment : AthleticMvpBindingFragment<
    SubscriptionPlansViewModel,
    ViewDataBinding,
    SubscriptionPlansContract.SubscriptionPlansViewState>() {

    override fun inflateBindingLayout(inflater: LayoutInflater): ViewDataBinding {
        return if (presenter.isSpecialOffer()) {
            FragmentSubscriptionPlansSpecialOfferBinding.inflate(inflater)
        } else {
            FragmentSubscriptionPlansBinding.inflate(inflater)
        }
    }

    override fun setupViewModel() = getViewModel<SubscriptionPlansViewModel> {
        parametersOf(
            navigator,
            SubscriptionPlansInitialData(getExtras())
        )
    }

    override fun renderState(viewState: SubscriptionPlansContract.SubscriptionPlansViewState) { }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            AuthenticationActivity.AUTH_RESULT_CODE -> presenter.launchNextScreen()
            SurveyActivity.ATTRIBUTION_SURVEY_REQUEST_CODE -> presenter.launchNextScreen()
        }
    }
}