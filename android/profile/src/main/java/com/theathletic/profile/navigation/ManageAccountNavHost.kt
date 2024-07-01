package com.theathletic.profile.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.theathletic.profile.ui.ManageAccountScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun ManageAccountNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = ManageAccountDestinations.ManageAccount.route,
        modifier = modifier,
    ) {
        composable(ManageAccountDestinations.ManageAccount.route) {
            ManageAccountScreen(koinViewModel())
        }
    }
}

sealed class ManageAccountDestinations(val route: String) {
    object ManageAccount : ManageAccountDestinations("manageAccount")
}