package com.theathletic.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.theathletic.extension.extGetString
import com.theathletic.ui.BaseView
import com.theathletic.utility.logging.ICrashLogHandler
import org.koin.android.ext.android.inject

@Deprecated("We should use AthleticFragment instead")
abstract class BaseFragment : Fragment(), BaseView {
    var fragmentVisible = true
        private set
    private val crashLogHandler by inject<ICrashLogHandler>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crashLogHandler.setCurrentFragmentKey(this::class.java.simpleName)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        crashLogHandler.setCurrentFragmentKey(this::class.java.simpleName)
        if (fragmentVisible)
            fragmentSubscribeStatusChange(true)
        super.onResume()
    }

    override fun onPause() {
        fragmentSubscribeStatusChange(false)
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        try {
            super.onSaveInstanceState(outState)
        } catch (e: Exception) {
            crashLogHandler.trackException(
                e,
                "FRAGMENT_SAVE_INSTANCE_STATE",
                "Fragment SaveInstanceState crash: ${this.javaClass}\n$e"
            )
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        fragmentVisible = !hidden
        fragmentSubscribeStatusChange(fragmentVisible)
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun getContext(): Context {
        return super.getContext()!!
    }

    override fun showToast(message: String) {
        Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
    }

    override fun showToast(stringRes: Int) {
        showToast(getString(stringRes))
    }

    override fun showSnackbar(message: String) {
        view?.let { view ->
            Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun showSnackbar(stringRes: Int) {
        showSnackbar(stringRes.extGetString())
    }

    override fun viewLifecycleOwnerProducer() = viewLifecycleOwner

    open fun onBackPressed(): Boolean {
        val lastFragment = childFragmentManager.fragments.lastOrNull { it.isResumed }

        return when {
            lastFragment is AthleticFragment && lastFragment.onBackPressed() -> true
            lastFragment is BaseFragment && lastFragment.onBackPressed() -> true
            else -> false
        }
    }

    open fun fragmentSubscribeStatusChange(shouldBeSubscribed: Boolean) {}

    fun getExtras(): Bundle? = activity?.intent?.extras
}