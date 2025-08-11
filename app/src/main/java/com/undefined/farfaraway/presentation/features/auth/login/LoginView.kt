package com.undefined.farfaraway.presentation.features.auth.login

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.undefined.farfaraway.R
import com.undefined.farfaraway.domain.entities.Response
import com.undefined.farfaraway.presentation.shared.navigation.enums.Routes

@Composable
fun LoginView(
    modifier: Modifier,
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val loginState by viewModel.isLoading.collectAsState()
    val context = LocalContext.current
    val userDataSaved by viewModel.userDataSaved.collectAsState()


    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        loginState?.let { state ->
            when (state) {
                is Response.Loading -> {
                    CircularProgressIndicator()
                }

                is Response.Error -> {
                    Toast.makeText(
                        context,
                        state.exception?.message ?: stringResource(id = R.string.auth_error_login),
                        Toast.LENGTH_LONG
                    ).show()
                    viewModel.resetInitState()
                }

                is Response.Success -> {
                    Toast.makeText(
                        context,
                        stringResource(id = R.string.auth_success_login),
                        Toast.LENGTH_SHORT
                    ).show()

                    LaunchedEffect(loginState, userDataSaved) {
                        if ( userDataSaved) {
                            println("Login successful and data saved, navigating to home...")
                            navController.navigate(Routes.HOME.name) {
                                popUpTo(navController.graph.startDestinationId) {
                                    inclusive = false
                                }
                            }
                            viewModel.resetInitState()                        }
                    }

                }

                else -> {}
            }
        }
    }
}