package com.undefined.farfaraway.presentation.features.profile.profile

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.undefined.farfaraway.domain.entities.UserType
import com.undefined.farfaraway.presentation.shared.navigation.enums.Routes

@Composable
fun ProfileContent(
    paddingValues: PaddingValues,
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    val currentUser by viewModel.currentUser.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val context = LocalContext.current


    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            // Header del perfil
            ProfileHeader(
                user = currentUser,
                onViewFullProfile = {
                    /*navController.navigate(Routes.MY_PROFILE.name)*/
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Sección de configuración de cuenta
            ProfileSection(title = "Cuenta") {
                ProfileMenuItem(
                    icon = Icons.Outlined.Edit,
                    title = "Editar perfil",
                    subtitle = "Actualiza tu información personal",
                    onClick = { navController.navigate(Routes.CONFIG_PROFILE.name) }
                )

                ProfileMenuDivider()

                ProfileMenuItem(
                    icon = Icons.Outlined.Lock,
                    title = "Cambiar contraseña",
                    subtitle = "Actualiza tu contraseña de acceso",
                    onClick = { /*navController.navigate(Routes.CHANGE_PASSWORD.name)*/ }
                )

                ProfileMenuDivider()

                ProfileMenuItem(
                    icon = Icons.Outlined.Person,
                    title = "Mi perfil completo",
                    subtitle = "Ver mi perfil como lo ven otros usuarios",
                    onClick = { /*navController.navigate(Routes.MY_PROFILE.name)*/ }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sección de notificaciones y pagos
            ProfileSection(title = "Preferencias") {
                ProfileMenuItem(
                    icon = Icons.Outlined.Notifications,
                    title = "Notificaciones",
                    subtitle = "Configura tus alertas y avisos",
                    onClick = { navController.navigate(Routes.NOTIFICATIONS.name) }
                )

                ProfileMenuDivider()

                ProfileMenuItem(
                    icon = Icons.Outlined.Payment,
                    title = "Pagos",
                    subtitle = "Gestiona tus métodos de pago",
                    onClick = { /*navController.navigate(Routes.PAYMENTS.name)*/ }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sección de soporte
            ProfileSection(title = "Soporte") {
                ProfileMenuItem(
                    icon = Icons.Outlined.Help,
                    title = "Ayuda",
                    subtitle = "Centro de ayuda, sugerencias y reporte de bugs o mal funcionamiento de la app",
                    onClick = { context.sendMail(to = "undefined.clt.1000@gmail.com") }
                )

                ProfileMenuDivider()

                ProfileMenuItem(
                    icon = Icons.Outlined.Info,
                    title = "Acerca de la app",
                    subtitle = "Esta aplicación está hecha por @AwesomeTeam. Versión de la app: 1.0.1",
                    onClick = {  }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Botón de cerrar sesión
            LogoutButton(
                onClick = { showLogoutDialog = true }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Dialog de confirmación para cerrar sesión
    if (showLogoutDialog) {
        LogoutConfirmationDialog(
            onConfirm = {
                showLogoutDialog = false
                viewModel.logout()
            },
            onDismiss = {
                showLogoutDialog = false
            }
        )
    }
}

@Composable
fun ProfileHeader(
    user: com.undefined.farfaraway.domain.entities.User?,
    onViewFullProfile: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .clickable { onViewFullProfile() }
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Foto de perfil
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            if (!user?.profileImageUrl.isNullOrEmpty()) {
                // TODO: Usar AsyncImage cuando implementes carga de imágenes
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar",
                    modifier = Modifier.size(60.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar por defecto",
                    modifier = Modifier.size(60.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Nombre del usuario
        Text(
            text = "${user?.firstName ?: ""} ${user?.lastName ?: ""}".trim().ifEmpty { "Usuario" },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Email del usuario
        Text(
            text = user?.email ?: "email@ejemplo.com",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Tipo de usuario y verificación
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Badge del tipo de usuario
            AssistChip(
                onClick = { },
                label = {
                    Text(
                        text = when (user?.userType) {
                            UserType.STUDENT.name -> "Estudiante"
                            UserType.LANDLORD.name -> "Arrendador"
                            UserType.ADMIN.name -> "Administrador"
                            else -> "Usuario"
                        }
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = when (user?.userType) {
                            UserType.STUDENT.name -> Icons.Default.School
                            UserType.LANDLORD.name -> Icons.Default.Home
                            UserType.ADMIN.name -> Icons.Default.AdminPanelSettings
                            else -> Icons.Default.Person
                        },
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            )

            // Estado de verificación de email
            if (user?.isEmailVerified == true) {
                AssistChip(
                    onClick = { },
                    label = { Text("Verificado") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        }

        // Rating si tiene reseñas
        if ((user?.totalReviews ?: 0) > 0) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "${user?.averageRating?.let { "%.1f".format(it) } ?: "0.0"} (${user?.totalReviews ?: 0} reseñas)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Indicador para ver perfil completo
        Text(
            text = "Toca para ver perfil completo",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ProfileSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surface,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(vertical = 8.dp)
        ) {
            content()
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Ir a $title",
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun ProfileMenuDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
    )
}

@Composable
fun LogoutButton(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.medium
                )
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Logout,
                contentDescription = "Cerrar sesión",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Cerrar sesión",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun LogoutConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Outlined.Logout,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error
            )
        },
        title = {
            Text(
                text = "Cerrar sesión",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Text(
                text = "¿Estás seguro de que quieres cerrar tu sesión? Tendrás que volver a iniciar sesión para acceder a tu cuenta.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Cerrar sesión")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        iconContentColor = MaterialTheme.colorScheme.error
    )
}

fun Context.sendMail(to: String) {
    try {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "message/rfc822"
        intent.putExtra(
            Intent.EXTRA_EMAIL, arrayOf(to),
        )
        intent.putExtra(
            Intent.EXTRA_SUBJECT, "Problemas con FarFarAway"
        )
        intent.setPackage("com.google.android.gm")
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(this, "no se encontró la aplicacion de Gmail.", Toast.LENGTH_SHORT).show()
    } catch (t: Throwable) {
        Toast.makeText(this, "Ocurrió un error al intentar enviar el correo.", Toast.LENGTH_SHORT).show()
    }
}