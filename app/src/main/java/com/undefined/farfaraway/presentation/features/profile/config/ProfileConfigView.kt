package com.undefined.farfaraway.presentation.features.profile.config

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun ProfileConfigView (
    modifier: Modifier,
    navController: NavController,
    viewModel: ProfileConfigViewModel = hiltViewModel()
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopStart
    ) {

        //GenericProgressLinearIndicator()

    }
}