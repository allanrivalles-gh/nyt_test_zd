package com.theathletic.user.ui

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.theathletic.R
import com.theathletic.databinding.FragmentPrivacyPolicyDialogBinding
import com.theathletic.extension.extSetClickableSpan
import com.theathletic.navigation.ScreenNavigator
import com.theathletic.utility.PrivacyRegion
import com.theathletic.utility.getPrivacyPolicyLink
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class PrivacyPolicyDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentPrivacyPolicyDialogBinding
    private val navigator by inject<ScreenNavigator> { parametersOf(requireActivity()) }
    internal lateinit var listener: PrivacyPolicyDialogListener

    companion object {
        private const val EXTRA_PRIVACY_REGION = "extra_privacy_region"
        fun newInstance(privacyRegion: PrivacyRegion) = PrivacyPolicyDialogFragment().apply {
            arguments = bundleOf(EXTRA_PRIVACY_REGION to privacyRegion)
        }
    }

    init {
        isCancelable = false
    }

    interface PrivacyPolicyDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPrivacyPolicyDialogBinding.inflate(inflater)
        val privacyRegion = try {
            arguments?.get(EXTRA_PRIVACY_REGION) as? PrivacyRegion ?: PrivacyRegion.Default
        } catch (e: Exception) {
            PrivacyRegion.Default
        }
        configureStrings(privacyRegion)
        binding.ctaAccept.setOnClickListener {
            listener.onDialogPositiveClick(this)
            dismiss()
        }

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            // Instantiate the PrivacyPolicyDialogListener so we can send events to the host
            listener = parentFragment as PrivacyPolicyDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement PrivacyPolicyDialogListener")
        }
    }

    override fun onDestroyView() {
        binding.ctaAccept.setOnClickListener(null)
        super.onDestroyView()
    }

    private fun configureStrings(privacyRegion: PrivacyRegion) {
        val strings = privacyStringMap[privacyRegion] ?: PrivacyStrings.Default
        binding.titleText.setText(strings.title)
        binding.ctaAccept.setText(strings.ctaText)
        configureMessageText(strings)
    }

    private fun configureMessageText(strings: PrivacyStrings) {
        val spannableString = SpannableString(getString(strings.message))
        spannableString.extSetClickableSpan(
            getString(strings.messageLinkText),
            useBold = false,
            removeUnderline = false
        ) {
            navigator.startOpenExternalLink(Uri.parse(getPrivacyPolicyLink()))
        }
        binding.messageText.setText(spannableString, TextView.BufferType.SPANNABLE)
        binding.messageText.movementMethod = LinkMovementMethod.getInstance()
    }

    private val privacyStringMap = mapOf(
        PrivacyRegion.Default to PrivacyStrings.Default,
        PrivacyRegion.Canada to PrivacyStrings.CA,
        PrivacyRegion.Australia to PrivacyStrings.AUS,
        PrivacyRegion.UK to PrivacyStrings.UK
    )

    private enum class PrivacyStrings(
        @StringRes val title: Int,
        @StringRes val message: Int,
        @StringRes val messageLinkText: Int,
        @StringRes val ctaText: Int
    ) {
        Default(
            R.string.dialog_privacy_refresh_title_default,
            R.string.dialog_privacy_refresh_message_default,
            R.string.dialog_privacy_refresh_link_text_default,
            R.string.dialog_privacy_refresh_cta_default
        ),
        CA(
            R.string.dialog_privacy_refresh_title_ca,
            R.string.dialog_privacy_refresh_message_ca,
            R.string.dialog_privacy_refresh_link_text_ca,
            R.string.dialog_privacy_refresh_cta_ca
        ),
        AUS(
            R.string.dialog_privacy_refresh_title_aus,
            R.string.dialog_privacy_refresh_message_aus,
            R.string.dialog_privacy_refresh_link_text_aus,
            R.string.dialog_privacy_refresh_cta_aus
        ),
        UK(
            R.string.dialog_privacy_refresh_title_uk,
            R.string.dialog_privacy_refresh_message_uk,
            R.string.dialog_privacy_refresh_link_text_uk,
            R.string.dialog_privacy_refresh_cta_uk
        )
    }
}