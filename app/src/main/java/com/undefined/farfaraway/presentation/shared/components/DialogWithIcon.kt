package com.undefined.farfaraway.presentation.shared.components

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.undefined.farfaraway.R

@Composable
fun DialogWithIcon(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    @StringRes dialogTitle: Int,
    @StringRes dialogText: Int,
    @DrawableRes icon: Int
) {
    AlertDialog(
        title = {
            Text(text = stringResource(id = dialogTitle))
        },
        icon = {
            Icon(painterResource(id = icon), contentDescription = "Example Icon")
        },
        text = {
            Text(text = stringResource(id = dialogText), textAlign = TextAlign.Center)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text(stringResource(id = R.string.auth_dialog_confirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(stringResource(id = R.string.auth_dialog_cancel))
            }
        }
    )
}

@Composable
fun MessageDialogWithIcon(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    @StringRes dialogTitle: Int,
    @StringRes dialogText: Int,
    icon: ImageVector
) {
    AlertDialog(
        title = {
            Text(text = stringResource(id = dialogTitle))
        },
        icon = {
            Icon(imageVector = icon , contentDescription = "Example Icon")
        },
        text = {
            Text(text = stringResource(id = dialogText), textAlign = TextAlign.Center)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text(stringResource(id = R.string.auth_dialog_confirm))
            }
        },
        dismissButton = {

        }
    )
}