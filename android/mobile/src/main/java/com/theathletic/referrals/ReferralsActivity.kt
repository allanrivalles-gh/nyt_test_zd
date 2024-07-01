package com.theathletic.referrals

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ImageSpan
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.Observer
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import com.theathletic.R
import com.theathletic.activity.BaseActivity
import com.theathletic.databinding.ActivityReferralsBinding
import com.theathletic.extension.extGetString
import com.theathletic.extension.safe
import com.theathletic.referrals.ReferralsContract.StateType.ERROR_FETCHING_SHARE_URL
import com.theathletic.referrals.ReferralsContract.StateType.FETCHING_SHARE_URL
import com.theathletic.referrals.ReferralsContract.StateType.INITIAL
import com.theathletic.referrals.ReferralsContract.StateType.NO_NETWORK
import com.theathletic.referrals.ReferralsContract.StateType.OPEN_SHARE_SHEET
import com.theathletic.referrals.ReferralsContract.StateType.OUT_OF_GUEST_PASSES
import com.theathletic.referrals.ReferralsContract.StateType.REQUEST_FOR_MORE_SENT
import com.theathletic.share.ShareBroadcastReceiver
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class ReferralsActivity : BaseActivity() {
    companion object {
        fun launch(
            context: Context,
            source: String
        ) = Intent(context, ReferralsActivity::class.java).apply {
            putExtra("source", source)
            context.startActivity(this)
        }
    }

    private lateinit var viewModel: ReferralsViewModel
    private lateinit var binding: ActivityReferralsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getViewModel { parametersOf(intent?.extras ?: Bundle()) }
        binding = ActivityReferralsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()

        viewModel.state.observe(
            this,
            Observer { state ->
                render(state)
            }
        )
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun render(state: ReferralsContract.State) {
        when (state.type) {
            INITIAL -> {
                val amount = "${state.passesRedeemed}/${state.totalPasses}"
                binding.amountRedeemed.text = amount
                binding.ctaButton.setOnClickListener {
                    viewModel.onClickSendGuestPass()
                }
            }
            FETCHING_SHARE_URL -> {
                bindProgressButton(binding.ctaButton)
                binding.ctaButton.showProgress {
                    progressColor = Color.WHITE
                }
                binding.ctaButton.isEnabled = false
            }
            NO_NETWORK -> {
                binding.ctaButton.hideProgress(R.string.referrals_cta_send_guest_pass.extGetString())
                binding.ctaButton.isEnabled = true
                Toast.makeText(this, R.string.global_network_offline, Toast.LENGTH_LONG).show()
            }
            ERROR_FETCHING_SHARE_URL -> {
                binding.ctaButton.hideProgress(R.string.referrals_cta_send_guest_pass.extGetString())
                binding.ctaButton.isEnabled = true
                Toast.makeText(this, R.string.global_error, Toast.LENGTH_LONG).show()
            }
            OPEN_SHARE_SHEET -> {
                openShareSheet(state)
            }
            OUT_OF_GUEST_PASSES -> {
                val amount = "${state.passesRedeemed}/${state.totalPasses}"
                binding.ctaButton.setOnClickListener {
                    viewModel.onClickRequestMorePasses()
                }
                binding.ctaButton.text = R.string.referrals_cta_request_more_passes.extGetString()
                binding.subtitleText.text = R.string.referrals_subtitle_out_of_passes.extGetString()
                binding.amountRedeemed.text = amount
            }
            REQUEST_FOR_MORE_SENT -> {
                binding.ctaButton.background = AppCompatResources.getDrawable(baseContext, R.drawable.cta_bg_white_border)
                binding.ctaButton.text = getCheckmarkString()
            }
        }.safe
    }

    private fun getCheckmarkString(): SpannableString {
        val image = ImageSpan(this, R.drawable.cta_checkmark, 2)
        val str = R.string.referrals_cta_request_sent.extGetString()
        val span = SpannableString("    $str")
        span.setSpan(image, 0, 1, 2)
        return span
    }

    private fun openShareSheet(state: ReferralsContract.State) {
        val amount = "${state.passesRedeemed}/${state.totalPasses}"
        val ctaText = if (state.passesRedeemed < state.totalPasses) {
            R.string.referrals_cta_send_guest_pass.extGetString()
        } else {
            R.string.referrals_cta_request_more_passes.extGetString()
        }
        binding.ctaButton.hideProgress(ctaText)
        binding.ctaButton.isEnabled = true
        binding.amountRedeemed.text = amount

        val title = R.string.referrals_share_subject.extGetString()
        val textToSend = R.string.referrals_share_body.extGetString(state.shareUrl)
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_SUBJECT, title)
            putExtra(Intent.EXTRA_TEXT, textToSend)
            // putExtra(Intent.EXTRA_STREAM, getCardUri())
            type = "text/plain"
        }
        val receiver = Intent(this, ShareBroadcastReceiver::class.java)
        receiver.putExtra(ShareBroadcastReceiver.ShareKey.REFERRALS.value, true)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            ShareBroadcastReceiver.REQUEST_CODE_REFERRAL,
            receiver,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        startActivity(
            Intent.createChooser(
                sendIntent,
                title,
                pendingIntent.intentSender
            )
        )
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }
}