package com.theathletic.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.rememberNavController
import com.theathletic.profile.navigation.ManageAccountNavHost
import com.theathletic.themes.AthTheme
import com.theathletic.themes.AthleticTheme
import com.theathletic.ui.DisplayPreferences
import org.koin.android.ext.android.inject

class ManageAccountActivity : AppCompatActivity() {

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, ManageAccountActivity::class.java)
        }
    }

    private val displayPreferences by inject<DisplayPreferences>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ManageAccountScaffold()
        }
    }

    @Composable
    fun ManageAccountScaffold() {
        val navController = rememberNavController()
        AthleticTheme(displayPreferences.shouldDisplayDayMode(this)) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(text = stringResource(R.string.profile_account_settings)) },
                        navigationIcon = {
                            IconButton(onClick = { finish() }) {
                                Icon(
                                    Icons.Default.ArrowBack,
                                    contentDescription = null,
                                    tint = AthTheme.colors.dark800
                                )
                            }
                        }
                    )
                }
            ) { paddingValues ->
                ManageAccountNavHost(
                    navController = navController,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}