package com.theathletic.activity

import android.os.Bundle
import com.theathletic.R
import com.theathletic.fragment.AthleticFragment

abstract class SingleFragmentActivity : BaseActivity() {
    abstract fun getFragment(): AthleticFragment?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_base)
        if (savedInstanceState == null) {
            getFragment()?.let { fragment ->
                supportFragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit()
            } ?: finish()
        }
    }
}