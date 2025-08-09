package com.undefined.farfaraway.presentation.features.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun HomeScreen(
    navController: NavController
) {
    Scaffold(
        content = { innerPadding ->
            HomeContent(paddingValues = innerPadding, navController = navController)
        }
    )
    HomeView(modifier = Modifier.fillMaxSize(), navController = navController)
}