package com.theathletic.injection

import android.os.Bundle
import com.theathletic.referrals.ReferralsViewModel
import com.theathletic.user.UserManager
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val referralsModule = module {
    viewModel { (extras: Bundle) ->
        ReferralsViewModel(
            get(),
            get(),
            UserManager,
            get(),
            extras
        )
    }
}