package com.undefined.farfaraway.presentation.shared.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.undefined.farfaraway.presentation.features.auth.login.LoginScreen
import com.undefined.farfaraway.presentation.features.auth.sign_up.SignUpScreen
import com.undefined.farfaraway.presentation.features.finance.bills.BillsScreen
import com.undefined.farfaraway.presentation.features.finance.finance.FinanceScreen
import com.undefined.farfaraway.presentation.features.home.HomeScreen
import com.undefined.farfaraway.presentation.features.notifications.NotificationsScreen
import com.undefined.farfaraway.presentation.features.profile.config.ProfileConfigScreen
import com.undefined.farfaraway.presentation.features.profile.profile.ProfileScreen
import com.undefined.farfaraway.presentation.features.rents.RentsScreen
import com.undefined.farfaraway.presentation.features.routes.RoutesScreen
import com.undefined.farfaraway.presentation.features.school.SchoolScreen
import com.undefined.farfaraway.presentation.shared.navigation.enums.Routes

fun NavGraphBuilder.mainRoutes(navController: NavController) {
    composable(Routes.LOGIN.name) { LoginScreen(navController) }
    composable(Routes.SIGN_UP.name) { SignUpScreen(navController) }

    composable(Routes.HOME.name) { HomeScreen(navController) }

    composable(Routes.CONFIG_PROFILE.name) { ProfileConfigScreen(navController) }
    composable(Routes.PROFILE.name) { ProfileScreen(navController) }

    composable(Routes.FINANCE.name) { FinanceScreen(navController) }
    composable(Routes.BILLS.name) { BillsScreen(navController) }

    composable(Routes.SCHOOL.name) { SchoolScreen(navController) }
    composable(Routes.RENTS.name) { RentsScreen(navController) }

    composable(Routes.NOTIFICATIONS.name) { NotificationsScreen(navController) }

    composable(Routes.ROUTES.name) { RoutesScreen(navController) }
}