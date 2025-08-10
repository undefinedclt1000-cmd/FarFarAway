package com.undefined.farfaraway.presentation.features.profile.myprofile

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@Composable
fun MyProfileView(
    modifier: Modifier,
    navController: NavController,
    viewModel: MyProfileViewModel = hiltViewModel()
) {
    val errorMessage by viewModel.errorMessage.collectAsState()
    val context = LocalContext.current

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Manejo de errores
        errorMessage?.let { message ->
            LaunchedEffect(message) {
                Toast.makeText(
                    context,
                    message,
                    Toast.LENGTH_LONG
                ).show()
                viewModel.clearError()
            }
        }
    }
}