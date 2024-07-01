package com.theathletic.debugtools

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SwitchCompat
import androidx.databinding.ObservableLong
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.theathletic.AthleticApplication
import com.theathletic.AthleticConfig
import com.theathletic.R
import com.theathletic.compass.codegen.CompassExperiment
import com.theathletic.databinding.FragmentDebugToolsBinding
import com.theathletic.debugtools.designsystem.DesignSystemActivity
import com.theathletic.debugtools.logs.ui.AnalyticsLogActivity
import com.theathletic.debugtools.userinfo.ui.DebugUserInfoActivity
import com.theathletic.event.DataChangeEvent
import com.theathletic.extension.ObservableString
import com.theathletic.extension.extGetColor
import com.theathletic.extension.extGetString
import com.theathletic.fragment.BaseBindingFragment
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.rooms.create.ui.CreateLiveRoomActivity
import com.theathletic.rooms.ui.LiveAudioRoomActivity
import com.theathletic.user.UserManager
import com.theathletic.utility.ActivityUtility
import com.theathletic.utility.Preferences
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import kotlin.system.exitProcess
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class DebugToolsFragment : BaseBindingFragment<DebugToolsViewModel, FragmentDebugToolsBinding>(), IDebugToolsView {
    private var adapter: DebugToolsAdapter? = null
    private val screenNavigator by inject<ScreenNavigator> { parametersOf(activity) }

    override fun inflateBindingLayout(inflater: LayoutInflater): FragmentDebugToolsBinding {
        return FragmentDebugToolsBinding.inflate(inflater)
    }

    @Suppress("LongMethod")
    override fun setupViewModel(): DebugToolsViewModel {
        val viewModel = getViewModel<DebugToolsViewModel>()
        lifecycle.addObserver(viewModel)
        viewModel.observeEvent(
            this,
            DataChangeEvent::class.java,
            Observer {
                onDataChangeEvent()
            }
        )
        viewModel.observeEvent(
            this,
            DebugToolsViewModel.StartCodeOfConductDevTools::class.java,
            Observer {
                startCodeOfConduct()
            }
        )
        viewModel.observeEvent(
            this,
            DebugToolsViewModel.ClearGlideCacheEvent::class.java,
            Observer {
                clearGlideCache()
            }
        )
        viewModel.observeEvent(
            this,
            DebugToolsViewModel.StartAnalyticsHistoryLogsActivity::class.java,
            Observer {
                startLogsActivity()
            }
        )
        viewModel.observeEvent(
            this,
            DebugToolsViewModel.StartDebugUserInfoActivity::class.java,
            Observer {
                Intent(context, DebugUserInfoActivity::class.java).run {
                    context.startActivity(this)
                }
            }
        )
        viewModel.observeEvent(
            this,
            DebugToolsViewModel.StartCreateLiveRoomActivity::class.java,
        ) {
            context.startActivity(
                CreateLiveRoomActivity.newIntent(context)
            )
        }

        viewModel.observeEvent(
            this,
            DebugToolsViewModel.StartAudioRoomDemoActivity::class.java,
            {
                val input = EditText(context).apply {
                    hint = "Room Name/ID"
                    setText("24jI9dvaOqBN")
                }
                AlertDialog.Builder(context)
                    .setTitle("Enter Room Name/ID")
                    .setView(input)
                    .setPositiveButton("Ok") { dialog, _ ->
                        val value = input.text.toString().trim()
                        if (value.isNotBlank()) {
                            context.startActivity(LiveAudioRoomActivity.newIntent(context, value))
                        }
                        dialog.dismiss()
                    }
                    .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                    .show()
            }
        )
        viewModel.observeEvent(
            this,
            DebugToolsViewModel.StartStylesheet::class.java,
            Observer {
                Intent(context, DesignSystemActivity::class.java).run {
                    context.startActivity(this)
                }
            }
        )
        viewModel.observeEvent(
            this,
            DebugToolsViewModel.StartBillingConfigActivity::class.java
        ) { screenNavigator.startBillingConfig() }
        return viewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.executePendingBindings()
        setupAdapter()
    }

    override fun onCustomButtonClick(onButtonClick: () -> Unit) {
        onButtonClick()
    }

    override fun onCustomSwitchChangedClick(
        customSwitch: View,
        switchedOn: () -> Unit,
        switchedOff: () -> Unit
    ) {
        if ((customSwitch as SwitchCompat).isChecked) {
            switchedOn()
        } else {
            switchedOff()
        }
    }

    override fun onFeatureSwitchChange(entryKey: String) {
        viewModel.onFeatureSwitchChange(entryKey)
    }

    /**
     * We are going to create PopupMenu and add all variants into that + Server Default option.
     */
    override fun onCompassVariantTextClick(view: View, data: DebugToolsCompassVariantSelectText) {
        val menu = PopupMenu(context, view)
        var itemId = 0
        val variantMap = CompassExperiment.variantMap
            .filterKeys { it.expName == data.experiment.name }
            .mapKeys { it.key.variantId }

        // Tt Add Server Default option to the menu
        menu.menu.add(0, itemId, 0, R.string.debug_tools_compass_selected_variant_server_default.extGetString())

        // Tt Add all other available variants to the menu
        variantMap.forEach { entry -> menu.menu.add(0, ++itemId, 0, entry.key) }

        // Tt Change the title and let viewModel know about the default value change
        menu.setOnMenuItemClickListener { selectedMenuItem ->
            val keyText = selectedMenuItem.title.toString()
            data.selectedVariant.set(keyText)
            viewModel.compassDefaultExperimentVariantChange(data.experiment, variantMap[keyText])
            showRestartTheAppDialog()
            true
        }

        menu.show()
    }

    override fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onSetClick(onSetClick: (value: String) -> Unit, currentValue: ObservableString) {
        onSetClick(currentValue.get() ?: AthleticConfig.REST_BASE_URL)
    }

    override fun onResetClick(onResetClick: () -> Unit, currentValue: ObservableString) {
        onResetClick()
        currentValue.set(AthleticConfig.REST_BASE_URL)
    }

    override fun onSendLink(onSendLink: (value: String?) -> Unit, deeplinkUrl: ObservableString) {
        onSendLink(deeplinkUrl.get())
    }

    override fun onBumpCountdownClick(
        onBumpCountdown: (value: ObservableLong) -> Unit,
        currentValue: ObservableLong
    ) {
        onBumpCountdown(currentValue)
    }

    override fun onResetCountdownClick(
        onResetCountdown: (value: ObservableLong) -> Unit,
        currentValue: ObservableLong
    ) {
        onResetCountdown(currentValue)
    }

    fun setupAdapter() {
        adapter = DebugToolsAdapter(this, viewModel.toolsRecyclerList)
        binding.recycler.adapter = adapter
    }

    private fun showRestartTheAppDialog() {
        val dialog = AlertDialog.Builder(context, R.style.Theme_Athletic_Dialog)
            .setTitle(R.string.debug_tools_section_compass_dialog_title)
            .setMessage(R.string.debug_tools_section_compass_dialog_message.extGetString())
            .setPositiveButton(R.string.debug_tools_section_compass_dialog_restart_the_app) { _, _ ->
                val startActivity = AthleticApplication.getContext().packageManager.getLaunchIntentForPackage(AthleticApplication.getContext().packageName)
                val pendingIntentId = 42
                val pendingIntent = PendingIntent.getActivity(
                    AthleticApplication.getContext(),
                    pendingIntentId,
                    startActivity,
                    PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                val alarm = AthleticApplication.getContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarm.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent)
                exitProcess(0)
            }
            .setNegativeButton(
                R.string.debug_tools_section_compass_dialog_log_out_the_user,
                { _, _ ->
                    Preferences.clear()
                    UserManager.logOutWithAuthenticationStart()
                }
            )
            .create()

        dialog.show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(R.color.red.extGetColor())
        dialog.findViewById<TextView>(android.R.id.message)?.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun onDataChangeEvent() {
        adapter?.notifyDataSetChanged()
    }

    private fun startCodeOfConduct() {
        ActivityUtility.startCodeOfConductSheetActivityForResult(context as Activity)
    }

    private fun startLogsActivity() {
        Intent(activity, AnalyticsLogActivity::class.java).run {
            startActivity(this)
        }
    }

    private fun clearGlideCache() {
        Single.just {
            Glide.get(AthleticApplication.getContext()).clearDiskCache()
        }.subscribeOn(Schedulers.io()).subscribe()
        Glide.get(AthleticApplication.getContext()).clearMemory()
    }
}