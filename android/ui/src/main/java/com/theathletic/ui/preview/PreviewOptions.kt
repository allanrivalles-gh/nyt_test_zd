package com.theathletic.ui.preview

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Preview(device = Devices.NEXUS_5, name = "Nexus 5")
@Preview(device = Devices.PIXEL_4_XL, name = "Pixel 4 XL")
annotation class DevicePreviewSmallAndLarge

@Preview(device = Devices.NEXUS_10, name = "Nexus 10")
@Preview(device = Devices.NEXUS_7, name = "Nexus 7")
annotation class DevicePreviewTablets

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
annotation class DayNightPreview