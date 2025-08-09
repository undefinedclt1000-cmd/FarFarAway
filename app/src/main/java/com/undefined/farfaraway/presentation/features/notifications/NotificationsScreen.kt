package com.undefined.farfaraway.presentation.features.notifications

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun NotificationsScreen(
    navController: NavController
) {
    Scaffold(
        content = { innerPadding ->
            NotificationsContent(paddingValues = innerPadding, navController = navController)
        }
    )
    NotificationsView(modifier = Modifier.fillMaxSize(), navController = navController)
}