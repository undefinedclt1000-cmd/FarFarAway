package com.undefined.farfaraway.presentation.features.routes

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun RoutesScreen(
    navController: NavController
) {
    Scaffold(
        content = { innerPadding ->
            RoutesContent(paddingValues = innerPadding, navController = navController)
        }
    )
    RoutesView(modifier = Modifier.fillMaxSize(), navController = navController)
}