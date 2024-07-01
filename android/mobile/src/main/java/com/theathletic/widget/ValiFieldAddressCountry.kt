package com.theathletic.widget

import com.mlykotom.valifi.fields.ValiFieldText
import com.theathletic.AthleticApplication
import com.theathletic.R

class ValiFieldAddressCountry @JvmOverloads constructor(defaultValue: String? = null) : ValiFieldText(defaultValue) {
    init {
        this.addNotEmptyValidator(AthleticApplication.getContext().resources.getString(R.string.validation_error_empty))
    }
}