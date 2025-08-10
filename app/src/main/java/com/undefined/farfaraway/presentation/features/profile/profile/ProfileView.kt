package com.undefined.farfaraway.presentation.features.profile.profile

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.undefined.farfaraway.domain.entities.Response
import com.undefined.farfaraway.presentation.shared.navigation.enums.Routes

@Composable
fun ProfileView(
    modifier: Modifier,
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val logoutState by viewModel.logoutState.collectAsState()
    val context = LocalContext.current

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        logoutState?.let { state ->
            when (state) {
                is Response.Loading -> {
                    CircularProgressIndicator()
                }

                is Response.Error -> {
                    LaunchedEffect(state) {
                        Toast.makeText(
                            context,
                            "Error al cerrar sesión: ${state.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                        viewModel.resetLogoutState()
                    }
                }

                is Response.Success -> {
                    LaunchedEffect(state) {
                        Toast.makeText(
                            context,
                            "Sesión cerrada correctamente",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Navegar al login y limpiar el back stack
                        navController.navigate(Routes.LOGIN.name) {
                            popUpTo(0) {
                                inclusive = true
                            }
                        }
                        viewModel.resetLogoutState()
                    }
                }

                Response.Idle -> TODO()
            }
        }
    }
}