package com.theathletic.debugtools

import androidx.databinding.ObservableArrayList
import com.theathletic.R
import org.alfonz.adapter.MultiDataBoundRecyclerAdapter

class DebugToolsAdapter(
    val view: IDebugToolsView,
    private val items: ObservableArrayList<DebugToolsBaseItem>
) : MultiDataBoundRecyclerAdapter(view, items) {
    override fun getItemLayoutId(position: Int): Int {
        return when (items[position]) {
            is RemoteConfigEntity -> R.layout.fragment_debug_tools_remoteconfig_item
            is DebugToolsSectionHeader -> R.layout.fragment_debug_tools_section_header_item
            is DebugToolsSectionSubHeader -> R.layout.fragment_debug_tools_section_sub_header_item
            is DebugToolsCustomButton -> R.layout.fragment_debug_tools_custom_button_item
            is DebugToolsCompassVariantSelectText -> R.layout.fragment_debug_tools_compass_variant_select_text_item
            is DebugToolsCustomSwitch -> R.layout.fragment_debug_tools_custom_switch_item
            is DebugToolsBaseUrlOverride -> R.layout.fragment_debug_tools_base_url_override_item
            is DebugToolsCountdown -> R.layout.fragment_debug_tools_countdown_item
            is DebugToolsSendDeeplink -> R.layout.fragment_debug_tools_deeplink_item
            is DebugToolsTextInput -> R.layout.fragment_debug_tools_text_input_item
            else -> R.layout.fragment_main_item_not_implemented
        }
    }
}