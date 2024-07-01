package com.theathletic.widget

import com.mlykotom.valifi.fields.ValiFieldText
import com.theathletic.AthleticApplication
import com.theathletic.R

class ValiFieldAddress @JvmOverloads constructor(defaultValue: String? = null) : ValiFieldText(defaultValue) {
    init {
        this.addMinLengthValidator(AthleticApplication.getContext().resources.getString(R.string.validation_error_min_length, 2), 2)
    }
}