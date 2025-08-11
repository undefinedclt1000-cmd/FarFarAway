package com.undefined.farfaraway.presentation.features.rents

import androidx.compose.animation.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RentsScreen(
    navController: NavController,
    viewModel: RentsViewModel = hiltViewModel()
) {
    var showAddPropertyDialog by remember { mutableStateOf(false) }

    val isOwner by viewModel.isOwner.collectAsState()
    val isAddingProperty by viewModel.isAddingProperty.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.addPropertyResult.collect { result ->
            if (result.isSuccess) {
                showAddPropertyDialog = false
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            AnimatedVisibility(
                visible = isOwner,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                FloatingActionButton(
                    onClick = { showAddPropertyDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Agregar propiedad"
                    )
                }
            }
        },
        content = { innerPadding ->
            RentsContent(
                paddingValues = innerPadding,
                navController = navController,
                viewModel = viewModel
            )
        }
    )

    if (showAddPropertyDialog) {
        AddPropertyDialog(
            onDismiss = { showAddPropertyDialog = false },
            onSave = { property ->
                viewModel.addProperty(property)
            },
            isLoading = isAddingProperty
        )
    }
}