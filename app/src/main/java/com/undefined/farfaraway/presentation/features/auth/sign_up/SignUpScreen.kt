package com.undefined.farfaraway.presentation.features.auth.sign_up

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import android.widget.Toast
import com.undefined.farfaraway.R
import com.undefined.farfaraway.domain.entities.Response
import com.undefined.farfaraway.presentation.shared.navigation.enums.Routes

@Composable
fun SignUpScreen(
    navController: NavController
) {
    val viewModel: SignUpViewModel = hiltViewModel()
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold { innerPadding ->
        Box {
            // Contenido principal del formulario
            SignUpContent(
                paddingValues = innerPadding,
                navController = navController,
                viewModel = viewModel
            )

            // Overlay de loading y manejo de estados
            isLoading?.let { loadingState ->
                when (loadingState) {
                    is Response.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is Response.Error -> {
                        Toast.makeText(
                            context,
                            loadingState.getErrorMessage(),
                            Toast.LENGTH_LONG
                        ).show()
                        viewModel.resetInitState()
                    }

                    is Response.Success -> {
                        Toast.makeText(
                            context,
                            stringResource(id = R.string.auth_success_register),
                            Toast.LENGTH_LONG
                        ).show()
                        navController.navigate(Routes.HOME.name) {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = false
                            }
                        }
                        viewModel.resetInitState()
                    }

                    Response.Idle -> TODO()
                }
            }
        }
    }
}