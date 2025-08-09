package com.undefined.farfaraway.presentation.features.school

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun SchoolScreen(
    navController: NavController
) {
    Scaffold(
        content = { innerPadding ->
            SchoolContent(paddingValues = innerPadding, navController = navController)
        }
    )
    SchoolView(modifier = Modifier.fillMaxSize(), navController = navController)
}