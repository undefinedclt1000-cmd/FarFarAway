package com.undefined.farfaraway.presentation.features.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun HomeView (
    modifier: Modifier,
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopStart
    ) {
        // Aquí puedes agregar elementos que se superpongan al contenido principal
        // Por ejemplo: loading indicators, floating action buttons, etc.

        // Si necesitas un indicador de progreso, descomenta la siguiente línea:
        // GenericProgressLinearIndicator()
    }
}