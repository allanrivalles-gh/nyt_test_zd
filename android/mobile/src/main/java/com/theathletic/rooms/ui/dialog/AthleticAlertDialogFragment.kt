package com.theathletic.rooms.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import com.theathletic.databinding.FragmentAthleticDialogBinding

class AthleticAlertDialogFragment(
    @StringRes val title: Int? = null,
    @StringRes val message: Int? = null,
) : DialogFragment() {

    private lateinit var binding: FragmentAthleticDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAthleticDialogBinding.inflate(inflater)

        binding.title = title
        binding.message = message

        return binding.root
    }
}