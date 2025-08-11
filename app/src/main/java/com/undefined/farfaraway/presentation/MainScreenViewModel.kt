package com.undefined.farfaraway.presentation

import androidx.lifecycle.ViewModel
import com.undefined.farfaraway.presentation.shared.navigation.enums.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
): ViewModel() {

    fun verifyRouteTop(currentRoute: String?): Boolean {
        return !(
                currentRoute == Routes.LOGIN.name ||
                        currentRoute == Routes.SIGN_UP.name ||
                        currentRoute == Routes.MY_PROFILE.name ||
                        currentRoute == Routes.FINANCE.name ||
                        currentRoute == Routes.BILLS.name ||
                        currentRoute == Routes.BILLS.name ||
                        currentRoute == Routes.NOTIFICATIONS.name ||
                        currentRoute == Routes.ROUTES.name ||
                        currentRoute == Routes.RENTS.name
                )
    }

    fun verifyRouteBottom(currentRoute: String?): Boolean {
        return !(currentRoute == Routes.LOGIN.name || currentRoute == Routes.SIGN_UP.name)
    }
}