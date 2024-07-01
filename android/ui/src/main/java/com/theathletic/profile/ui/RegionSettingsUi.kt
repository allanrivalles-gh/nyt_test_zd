package com.theathletic.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.RadioButton
import androidx.compose.material.RadioButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.theathletic.profile.ui.ProfilePreviewData.regionInteractor
import com.theathletic.profile.ui.ProfilePreviewData.regionSettingsUiModel
import com.theathletic.themes.AthTextStyle
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.R
import com.theathletic.ui.ResourceString
import com.theathletic.ui.ResourceString.StringWithParams
import com.theathletic.ui.UiModel
import com.theathletic.ui.asString

data class RegionSettingsUiModel(
    val id: String,
    val regions: List<RegionOptions>,
    val selectedIndex: Int
) : UiModel {
    override val stableId = "RegionSettingsUi:$id"

    interface Interactor {
        fun onBackClick()
        fun regionOptionSelected(region: RegionOptions)
    }
}

enum class RegionOptions(val title: ResourceString) {
    NORTH_AMERICA(StringWithParams(R.string.region_option_north_america)),
    INTERNATIONAL(StringWithParams(R.string.region_option_international))
}

@Composable
fun RegionSettings(regions: List<RegionOptions>, selectedIndex: Int, interactor: RegionSettingsUiModel.Interactor) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = AthTheme.colors.dark200)
    ) {
        Toolbar {
            interactor.onBackClick()
        }
        SetupRegionOptions(regions, selectedIndex, interactor)
        Text(
            text = stringResource(id = R.string.region_note),
            style = AthTextStyle.Calibre.Utility.Medium.ExtraSmall,
            color = AthTheme.colors.dark600,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
private fun SetupRegionOptions(
    regions: List<RegionOptions>,
    selectedIndex: Int,
    interactor: RegionSettingsUiModel.Interactor
) {
    var selectedOption = regions[selectedIndex]
    regions.forEachIndexed { index, option ->
        Row(
            Modifier
                .fillMaxWidth()
                .selectable(
                    selected = (option == selectedOption),
                    onClick = { selectedOption = option }
                ),
            verticalAlignment = CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {

            RadioButton(
                colors = defaultRadioButtonColors(),
                selected = (option == selectedOption),
                onClick = {
                    selectedOption = option
                    interactor.regionOptionSelected(option)
                },
                modifier = Modifier.padding(start = 16.dp)
            )

            Text(
                text = option.title.asString(),
                style = AthTextStyle.Calibre.Utility.Regular.ExtraLarge,
                color = AthTheme.colors.dark800,
                modifier = Modifier.padding(end = 16.dp)
            )
        }
    }
}

@Composable
private fun Toolbar(
    onBackClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(50.dp)
            .fillMaxWidth()
            .background(AthTheme.colors.dark200)
    ) {
        Row(Modifier.align(Alignment.CenterStart)) {
            IconButton(onClick = onBackClicked) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = AthTheme.colors.dark800
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center),
        ) {
            Text(
                text = stringResource(id = R.string.profile_region_preferences),
                style = AthTextStyle.Slab.Bold.Small,
                fontSize = 20.sp,
                maxLines = 1,
                color = AthTheme.colors.dark800,
            )
        }
    }
}

@Composable
private fun defaultRadioButtonColors() = RadioButtonDefaults.colors(
    selectedColor = AthTheme.colors.dark800,
    unselectedColor = AthTheme.colors.dark800,
    disabledColor = AthTheme.colors.dark800
)

@Preview
@Composable
private fun RegionSettings_Preview() {
    RegionSettings(
        regions = regionSettingsUiModel.regions,
        selectedIndex = regionSettingsUiModel.selectedIndex,
        interactor = regionInteractor
    )
}

@Preview
@Composable
private fun RegionSettings_PreviewLight() {
    AthleticTheme(lightMode = true) {
        RegionSettings(
            regions = regionSettingsUiModel.regions,
            selectedIndex = regionSettingsUiModel.selectedIndex,
            interactor = regionInteractor
        )
    }
}