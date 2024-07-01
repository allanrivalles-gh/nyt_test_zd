package com.theathletic.profile.legacy.account.ui

import android.app.Dialog
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.SpannableString
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import com.theathletic.AthleticConfig
import com.theathletic.R
import com.theathletic.analytics.newarch.Analytics
import com.theathletic.analytics.newarch.Event
import com.theathletic.analytics.newarch.track
import com.theathletic.databinding.FragmentManageAccountBinding
import com.theathletic.extension.extAddOnPropertyChangedCallback
import com.theathletic.extension.extGetString
import com.theathletic.extension.extSetClickableSpan
import com.theathletic.fragment.AthleticBindingFragment
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.utility.ActivityUtility
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class ManageAccountFragment :
    AthleticBindingFragment<ManageAccountViewModel, FragmentManageAccountBinding>(),
    ManageAccountView {

    private val analytics by inject<Analytics>()
    private val navigator by inject<ScreenNavigator> { parametersOf(requireActivity()) }

    private var saveItem: MenuItem? = null
    private var isBackPressFromDialog = false

    override fun setupViewModel() = getViewModel<ManageAccountViewModel> { parametersOf(navigator) }

    override fun inflateBindingLayout(
        inflater: LayoutInflater
    ) = FragmentManageAccountBinding.inflate(inflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        viewModel.observe<ShowGooglePlaySubscription>(this) {
            ActivityUtility.startManageSubscriptionActivity(context)
        }
        viewModel.observe<ShowManageSubscriptionDialog>(this) {
            showManageSubscriptionDialog()
        }
        viewModel.observe<ShowDeleteAccountDialog>(this) {
            showDeleteAccountDialog()
        }
        viewModel.observe<ShowDeleteAccountConfirmationDialog>(this) {
            showDeleteAccountConfirmationDialog()
        }
        viewModel.observe<ShowDeleteAccountSuccessDialog>(this) {
            showDeleteAccountSuccessDialog()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        analytics.track(Event.ManageAccount.View())

        viewModel.valuesChanged.extAddOnPropertyChangedCallback { _, _, _ ->
            saveItem?.isEnabled = viewModel.valuesChanged.get()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_manage_account, menu)
        saveItem = menu.findItem(R.id.action_save)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.action_save -> {
                analytics.track(Event.ManageAccount.Click(element = "save_profile"))
                viewModel.saveUserChanges()
                view?.let {
                    val inputMethodManager =
                        requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
                }
                true
            }
            else -> false
        }
    }

    override fun onBackPressed(): Boolean {
        return if (viewModel.valuesChanged.get() && !isBackPressFromDialog) {
            isBackPressFromDialog = true
            showDiscardDialog()
            true
        } else {
            super.onBackPressed()
        }
    }

    private fun showDiscardDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.account_discard_changes_title)
            .setMessage(R.string.account_discard_changes_message)
            .setCancelable(true)
            .setNegativeButton(R.string.account_action_cancel) { dialog, _ ->
                isBackPressFromDialog = false
                dialog.dismiss()
            }
            .setPositiveButton(R.string.account_action_discard) { _, _ ->
                analytics.track(Event.ManageAccount.Click(element = "discard_update"))
                requireActivity().onBackPressed()
            }
            .setOnCancelListener { isBackPressFromDialog = false }
            .create()
            .show()
    }

    private fun showManageSubscriptionDialog() {
        val message = SpannableString(
            R.string.dialog_manage_subscription_description
                .extGetString(AthleticConfig.ATHLETIC_SETTINGS)
        ).apply {
            extSetClickableSpan(AthleticConfig.ATHLETIC_SETTINGS) {
                ActivityUtility.startCustomTabsActivity(context, AthleticConfig.ATHLETIC_SETTINGS)
            }
        }

        analytics.track(Event.Profile.Click(element = "manage_subscriptions"))

        AlertDialog.Builder(context)
            .setTitle(R.string.dialog_manage_subscription_title)
            .setMessage(message)
            .setPositiveButton(R.string.notification_dialog_cta_positive, null)
            .create()
            .show()
    }

    private fun showDeleteAccountDialog() {
        analytics.track(Event.Profile.Click(element = "delete_account"))

        AlertDialog.Builder(context)
            .setTitle(R.string.dialog_delete_account_title)
            .setMessage(R.string.dialog_delete_account_confirmation_message)
            .setPositiveButton(R.string.dialog_delete_account_continue) { _, _ -> viewModel.onDeleteAccountContinueClicked() }
            .setNegativeButton(R.string.dialog_delete_account_cancel, null)
            .create()
            .show()
    }

    private fun showDeleteAccountConfirmationDialog() {
        val dialog = AlertDialog.Builder(context)
            .setTitle(R.string.dialog_delete_account_confirmation_title)
            .setMessage(R.string.dialog_delete_account_confirmation_description)
            .setPositiveButton(R.string.dialog_delete_account_confirmation) { _, _ -> viewModel.onDeleteAccountConfirmationClicked() }
            .setNegativeButton(R.string.dialog_delete_account_cancel, null)
            .create()

        dialog.show()
        dialog.getButton(Dialog.BUTTON_POSITIVE).setTextColor(context.getColor(R.color.red))
    }

    private fun showDeleteAccountSuccessDialog() = AlertDialog.Builder(context)
        .setTitle(R.string.dialog_delete_account_success_title)
        .setMessage(R.string.dialog_delete_account_success_message)
        .setPositiveButton(R.string.dialog_delete_account_success_ok) { _, _ -> viewModel.onDeleteAccountSuccessDialogDismissed() }
        .setOnDismissListener { _ -> viewModel.onDeleteAccountSuccessDialogDismissed() }
        .create()
        .show()

    override fun onManageAccountsClicked() {
        viewModel.onManageAccountsClicked()
    }

    override fun onSwitchSubscriptionClicked() {
        viewModel.onSwitchToAnnualPlanClicked(requireActivity())
    }

    override fun onDeleteAccountClicked() {
        viewModel.onDeleteAccountClicked()
    }

    override fun onManagePrivacySettingsClicked() {
        viewModel.onManagePrivacySettingsClicked()
    }
}