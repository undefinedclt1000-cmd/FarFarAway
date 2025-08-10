package com.undefined.farfaraway.presentation.shared.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/**
 * Componente genérico para un campo de texto personalizable en Jetpack Compose.
 *
 * @param value Valor actual del campo de texto.
 * @param onValueChange Callback para manejar cambios en el valor del campo de texto.
 * @param leadingIcon Icono que se mostrará a la izquierda del campo de texto.
 * @param keyboardType Tipo de teclado para el campo de texto.
 * @param placeholder Texto de marcador de posición para el campo de texto.
 * @param action Acción IME (por ejemplo, "Hecho", "Siguiente") para el teclado.
 * @param errorMessage Mensaje de error opcional que se mostrará debajo del campo de texto si hay un error.
 *                     Si es null, el campo de texto no mostrará ningún mensaje de error.
 * @param modifier Modificador opcional para personalizar la apariencia del campo de texto.
 */

@Composable
fun GenericTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    leadingIcon: ImageVector? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    @StringRes placeholder: Int,
    action: ImeAction = ImeAction.Default,
    errorMessage: String? = null,
    maxLines: Int = 1,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = { onValueChange(it) },
        modifier = modifier.fillMaxWidth(),
        label = { Text(text = stringResource(id = placeholder)) },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = keyboardType,
            imeAction = action
        ),
        singleLine = true,
        maxLines = maxLines,
        minLines = minLines,
        leadingIcon = leadingIcon?.let {
            {
                Icon(imageVector =  leadingIcon, contentDescription = "")
            }
        },
        isError = errorMessage != null,
    )

    errorMessage?.let {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
        )
    }
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
    )

}
