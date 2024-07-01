package com.theathletic.ui.list

import com.theathletic.ui.UiModel

interface MutableDataAdapter {
    fun updateData(data: List<UiModel>)
}