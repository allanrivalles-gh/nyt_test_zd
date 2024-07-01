package com.theathletic.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.WindowCompat
import androidx.databinding.Observable
import com.google.android.material.snackbar.Snackbar
import com.theathletic.R
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.conduct.CodeOfConductSheetActivity
import com.theathletic.debugtools.DebugPreferences
import com.theathletic.extension.extLogError
import com.theathletic.fragment.AthleticFragment
import com.theathletic.fragment.BaseFragment
import com.theathletic.manager.PodcastManager
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.podcast.ui.widget.PodcastMiniPlayer
import com.theathletic.ui.BaseView
import com.theathletic.ui.gallery.ImageGalleryActivity
import com.theathletic.utility.logging.ICrashLogHandler
import kotlinx.coroutines.Job
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf
import timber.log.Timber

@SuppressLint("Registered")
abstract class BaseActivity : AppCompatActivity(), BaseView {
    private var podcastMiniPlayerView: View? = null
    private val miniPlayerCallback = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            showMiniPlayerIfNeeded()
        }
    }
    private var mToolbarHashCode = 0
    private var analyticsJob: Job? = null
    private val debugPreferences by inject<DebugPreferences>()
    protected val crashLogHandler by inject<ICrashLogHandler>()
    private val analytics by inject<Analytics>()
    protected val navigator by inject<ScreenNavigator> { parametersOf(this@BaseActivity) }

    /**
     * Override to true if the Activity will be responsible for adjusting the content to the status
     * bar, navigation bar, and keyboard.
     */
    open var handlesSystemInsets = false

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.i("[Activity] ${this.javaClass.simpleName}")
        super.onCreate(savedInstanceState)

        setOverrideTransition()

        if (handlesSystemInsets) {
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }

        crashLogHandler.setCurrentActivityKey(this::class.java.simpleName)

        if (shouldScreenBePortraitOnly()) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        PodcastManager.shouldBeMiniPlayerVisible.addOnPropertyChangedCallback(miniPlayerCallback)

        window.statusBarColor = getColor(getStatusBarColor())
    }

    open fun setOverrideTransition() {
        // Tt Override default opening transition
        overridePendingTransition(R.anim.alpha_in, R.anim.alpha_out)
    }

    @ColorRes
    open fun getStatusBarColor() = R.color.ath_grey_70

    override fun onResume() {
        crashLogHandler.setCurrentActivityKey(this::class.java.simpleName)
        showMiniPlayerIfNeeded()
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        analyticsJob?.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        PodcastManager.shouldBeMiniPlayerVisible.removeOnPropertyChangedCallback(miniPlayerCallback)
    }

    override fun finish() {
        super.finish()

        setOverrideTransition()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data != null) {
            super.onActivityResult(requestCode, resultCode, data)

            for (fragment in supportFragmentManager.fragments) {
                fragment?.onActivityResult(requestCode, resultCode, data)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        val fragmentList = supportFragmentManager.fragments

        var handled = false
        for (fragment in fragmentList) {
            if (fragment is AthleticFragment && fragment.onBackPressed()) {
                handled = true
                break
            } else if (fragment is BaseFragment && fragment.onBackPressed()) {
                handled = true
                break
            }
        }

        if (!handled) {
            super.onBackPressed()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        try {
            super.onSaveInstanceState(outState)
        } catch (e: Exception) {
            crashLogHandler.trackException(
                e,
                "ACTIVITY_SAVE_INSTANCE_STATE",
                "Activity SaveInstanceState crash: ${this.javaClass}\n$e"
            )
        }
    }

    override fun showSnackbar(message: String) {
        findViewById<View>(android.R.id.content)?.let { view ->
            Snackbar.make(view, message, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun showSnackbar(stringRes: Int) {
        findViewById<View>(android.R.id.content)?.let { view ->
            Snackbar.make(view, getString(stringRes), Snackbar.LENGTH_LONG).show()
        }
    }

    override fun showToast(stringRes: Int) = Toast.makeText(this, stringRes, Toast.LENGTH_LONG).show()

    override fun showToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_LONG).show()

    override fun viewLifecycleOwnerProducer() = this

    open fun shouldScreenBePortraitOnly(): Boolean {
        // https://stackoverflow.com/a/50832408
        return when (this.javaClass.simpleName) {
            CodeOfConductSheetActivity::class.java.simpleName -> false
            ImageGalleryActivity::class.java.simpleName -> false
            else -> resources.getBoolean(R.bool.portrait_only)
        }
    }

    fun setupActionBar(
        title: CharSequence? = null,
        toolbar: Toolbar
    ): ActionBar? {
        // this check is here because if 2 fragments with different indicators share a toolbar in activity,
        // it caused bug that back icon was not shown
        if (mToolbarHashCode != toolbar.hashCode()) {
            setSupportActionBar(toolbar)
        }

        val customTitleTextview = toolbar.findViewById<TextView>(R.id.toolbar_custom_title)

        val actionBar = supportActionBar?.apply {
            setDisplayUseLogoEnabled(false)
            setDisplayShowTitleEnabled(customTitleTextview == null)
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
            setHomeAsUpIndicator(null)

            if (title != null) {
                this.title = title
                customTitleTextview?.text = title
            }
        }

        mToolbarHashCode = toolbar.hashCode()
        return actionBar
    }

    private fun showMiniPlayerIfNeeded() {
        try {
            if (PodcastManager.shouldBeMiniPlayerVisible.get()) {
                if (podcastMiniPlayerView == null) {
                    podcastMiniPlayerView = PodcastMiniPlayer.create(this, findViewById(android.R.id.content))
                }
            } else {
                findViewById<ViewGroup>(android.R.id.content).removeView(podcastMiniPlayerView)
                podcastMiniPlayerView = null
            }
        } catch (e: Exception) {
            e.extLogError()
        }
    }
}