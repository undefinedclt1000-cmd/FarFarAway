package com.undefined.farfaraway.presentation.features.auth.sign_up

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun SignUpScreen(
    navController: NavController
) {
    Scaffold(
        content = { innerPadding ->
            SignUpContent(paddingValues = innerPadding, navController = navController)
        }
    )
    SignUpView(modifier = Modifier.fillMaxSize(), navController = navController)
}