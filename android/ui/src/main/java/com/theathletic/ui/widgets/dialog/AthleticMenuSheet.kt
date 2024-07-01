package com.theathletic.ui.widgets.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.theathletic.ui.R
import com.theathletic.ui.databinding.AthleticMenuSheetContainerBinding
import com.theathletic.ui.databinding.AthleticMenuSheetRowBinding
import kotlinx.parcelize.Parcelize

class AthleticMenuSheet : BottomSheetDialogFragment() {

    @Parcelize
    data class Entry(
        @DrawableRes val iconRes: Int,
        @StringRes val textRes: Int
    ) : Parcelable

    companion object {
        private const val ARG_ENTRIES = "entries"

        fun newInstance(entries: List<Entry>): AthleticMenuSheet {
            return AthleticMenuSheet().apply {
                arguments = bundleOf(ARG_ENTRIES to entries.toTypedArray())
            }
        }
    }

    var listeners: Map<Entry, () -> Unit>? = null
    var onCancelListener = { }
    private var entries: Array<Entry> = emptyArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Here we are recreated after system death, with no listener we should just not show.
        if (listeners.isNullOrEmpty()) {
            dismiss()
        }

        entries = arguments?.getParcelableArray(ARG_ENTRIES) as? Array<Entry> ?: emptyArray()

        if (entries.isEmpty()) {
            dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = AthleticMenuSheetContainerBinding.inflate(inflater, container, false)

        for (entry in entries) {
            AthleticMenuSheetRowBinding.inflate(inflater, binding.container, true).apply {
                data = entry
                interactor = this@AthleticMenuSheet
            }
        }

        return binding.root
    }

    override fun onCancel(dialog: DialogInterface) {
        onCancelListener()
        super.onCancel(dialog)
    }

    fun onOptionsItemSelected(entry: Entry) {
        listeners?.get(entry)?.invoke()
        dismiss()
    }

    override fun getTheme() = R.style.Widget_Ath_MenuSheet
}