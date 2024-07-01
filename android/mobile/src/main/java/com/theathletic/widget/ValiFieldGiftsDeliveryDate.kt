package com.theathletic.widget

import com.mlykotom.valifi.fields.ValiFieldDate
import com.theathletic.utility.datetime.DateUtilityImpl
import java.util.Calendar

class ValiFieldGiftsDeliveryDate : ValiFieldDate() {
    override fun convertValueToString(calendar: Calendar): String {
        return DateUtilityImpl.formatGiftsDeliveryDate(calendar.time.time)
    }
}