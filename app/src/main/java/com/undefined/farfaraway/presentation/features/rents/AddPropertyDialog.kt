package com.undefined.farfaraway.presentation.features.rents

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.undefined.farfaraway.domain.entities.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPropertyDialog(
    onDismiss: () -> Unit,
    onSave: (Property) -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    var property by remember { mutableStateOf(createDefaultProperty()) }
    var currentStep by remember { mutableIntStateOf(0) }
    val totalSteps = 5 // Aumenté a 5 pasos para incluir imágenes

    // Estados para validación mejorada
    var titleError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }
    var addressError by remember { mutableStateOf<String?>(null) }
    var priceError by remember { mutableStateOf<String?>(null) }
    var contactError by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Header mejorado
                PropertyDialogHeader(
                    currentStep = currentStep,
                    totalSteps = totalSteps,
                    onClose = onDismiss,
                    title = "Agregar Propiedad"
                )

                // Content con validación mejorada
                Box(
                    modifier = Modifier.weight(1f)
                ) {
                    when (currentStep) {
                        0 -> BasicInfoStep(
                            property = property,
                            onPropertyChange = { property = it },
                            titleError = titleError,
                            descriptionError = descriptionError,
                            addressError = addressError,
                            priceError = priceError,
                            onTitleErrorChange = { titleError = it },
                            onDescriptionErrorChange = { descriptionError = it },
                            onAddressErrorChange = { addressError = it },
                            onPriceErrorChange = { priceError = it }
                        )
                        1 -> PropertyDetailsStep(
                            property = property,
                            onPropertyChange = { property = it }
                        )
                        2 -> UtilitiesAndAmenitiesStep(
                            property = property,
                            onPropertyChange = { property = it }
                        )
                        3 -> ContactInfoStep(
                            property = property,
                            onPropertyChange = { property = it },
                            contactError = contactError,
                            onContactErrorChange = { contactError = it }
                        )
                        4 -> ImagesStep( // Nuevo paso para imágenes
                            property = property,
                            onPropertyChange = { property = it }
                        )
                    }
                }

                // Footer con validación mejorada
                PropertyDialogFooter(
                    currentStep = currentStep,
                    totalSteps = totalSteps,
                    isLoading = isLoading,
                    onPrevious = { if (currentStep > 0) currentStep-- },
                    onNext = {
                        val (isValid, errors) = validateCurrentStep(currentStep, property)

                        // Limpiar errores previos
                        titleError = null
                        descriptionError = null
                        addressError = null
                        priceError = null
                        contactError = null

                        // Aplicar nuevos errores si existen
                        errors.forEach { error ->
                            when (error.field) {
                                "title" -> titleError = error.message
                                "description" -> descriptionError = error.message
                                "address" -> addressError = error.message
                                "price" -> priceError = error.message
                                "contact" -> contactError = error.message
                            }
                        }

                        if (isValid) {
                            if (currentStep < totalSteps - 1) {
                                currentStep++
                            } else {
                                onSave(property)
                            }
                        }
                    },
                    onSave = { onSave(property) }
                )
            }
        }
    }
}

@Composable
private fun PropertyDialogHeader(
    currentStep: Int,
    totalSteps: Int,
    onClose: () -> Unit,
    title: String
) {
    Column {
        // Title bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar")
            }
        }

        // Progress indicator mejorado
        LinearProgressIndicator(
            progress = { (currentStep + 1).toFloat() / totalSteps },
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary
        )

        // Step indicator actualizado
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StepIndicator("Básico", 0, currentStep, totalSteps)
            StepIndicator("Detalles", 1, currentStep, totalSteps)
            StepIndicator("Servicios", 2, currentStep, totalSteps)
            StepIndicator("Contacto", 3, currentStep, totalSteps)
            StepIndicator("Fotos", 4, currentStep, totalSteps)
        }
    }
}

@Composable
private fun StepIndicator(
    title: String,
    stepIndex: Int,
    currentStep: Int,
    totalSteps: Int
) {
    val isActive = stepIndex == currentStep
    val isCompleted = stepIndex < currentStep

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(if (totalSteps > 4) 60.dp else 80.dp)
    ) {
        Surface(
            modifier = Modifier.size(28.dp),
            shape = MaterialTheme.shapes.small,
            color = when {
                isCompleted -> MaterialTheme.colorScheme.primary
                isActive -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                } else {
                    Text(
                        text = (stepIndex + 1).toString(),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isActive)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = if (isActive || isCompleted)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Mejoré el paso de información básica con validación en tiempo real
@Composable
private fun BasicInfoStep(
    property: Property,
    onPropertyChange: (Property) -> Unit,
    titleError: String?,
    descriptionError: String?,
    addressError: String?,
    priceError: String?,
    onTitleErrorChange: (String?) -> Unit,
    onDescriptionErrorChange: (String?) -> Unit,
    onAddressErrorChange: (String?) -> Unit,
    onPriceErrorChange: (String?) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            OutlinedTextField(
                value = property.title,
                onValueChange = {
                    onPropertyChange(property.copy(title = it))
                    if (it.isNotBlank()) {
                        onTitleErrorChange(null)
                    } else if (it.length > 100) {
                        onTitleErrorChange("El título no puede exceder 100 caracteres")
                    }
                },
                label = { Text("Título de la propiedad *") },
                placeholder = { Text("Ej: Cuarto cerca de la universidad") },
                modifier = Modifier.fillMaxWidth(),
                isError = titleError != null,
                supportingText = titleError?.let { { Text(it) } },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
        }

        item {
            OutlinedTextField(
                value = property.description,
                onValueChange = {
                    onPropertyChange(property.copy(description = it))
                    if (it.isNotBlank()) {
                        onDescriptionErrorChange(null)
                    } else if (it.length > 500) {
                        onDescriptionErrorChange("La descripción no puede exceder 500 caracteres")
                    }
                },
                label = { Text("Descripción *") },
                placeholder = { Text("Describe la propiedad, ubicación, características...") },
                modifier = Modifier.fillMaxWidth(),
                isError = descriptionError != null,
                supportingText = descriptionError?.let { { Text(it) } } ?: {
                    Text("${property.description.length}/500 caracteres")
                },
                minLines = 3,
                maxLines = 5,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
        }

        item {
            OutlinedTextField(
                value = property.address,
                onValueChange = {
                    onPropertyChange(property.copy(address = it))
                    if (it.isNotBlank()) onAddressErrorChange(null)
                },
                label = { Text("Dirección *") },
                placeholder = { Text("Ej: Calle 123, Colonia Centro") },
                modifier = Modifier.fillMaxWidth(),
                isError = addressError != null,
                supportingText = addressError?.let { { Text(it) } },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                leadingIcon = {
                    Icon(Icons.Default.LocationOn, contentDescription = null)
                }
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = if (property.monthlyRent > 0) property.monthlyRent.toInt().toString() else "",
                    onValueChange = { input ->
                        val price = input.toDoubleOrNull() ?: 0.0
                        onPropertyChange(property.copy(monthlyRent = price))
                        when {
                            input.isBlank() -> onPriceErrorChange("El precio es obligatorio")
                            price <= 0 -> onPriceErrorChange("El precio debe ser mayor a 0")
                            price > 50000 -> onPriceErrorChange("Precio muy alto, verifica")
                            else -> onPriceErrorChange(null)
                        }
                    },
                    label = { Text("Precio mensual *") },
                    placeholder = { Text("0") },
                    modifier = Modifier.weight(1f),
                    isError = priceError != null,
                    supportingText = priceError?.let { { Text(it) } },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    leadingIcon = {
                        Text("$", modifier = Modifier.padding(start = 12.dp))
                    }
                )

                OutlinedTextField(
                    value = if (property.deposit > 0) property.deposit.toInt().toString() else "",
                    onValueChange = {
                        val deposit = it.toDoubleOrNull() ?: 0.0
                        onPropertyChange(property.copy(deposit = deposit))
                    },
                    label = { Text("Depósito") },
                    placeholder = { Text("0") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    leadingIcon = {
                        Text("$", modifier = Modifier.padding(start = 12.dp))
                    }
                )
            }
        }

        // Agregué campo para coordenadas (opcional)
        item {
            Text(
                text = "Ubicación (Opcional)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = property.latitude?.toString() ?: "",
                    onValueChange = {
                        val lat = it.toDoubleOrNull()
                        onPropertyChange(property.copy(latitude = lat))
                    },
                    label = { Text("Latitud") },
                    placeholder = { Text("19.4326") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                OutlinedTextField(
                    value = property.longitude?.toString() ?: "",
                    onValueChange = {
                        val lng = it.toDoubleOrNull()
                        onPropertyChange(property.copy(longitude = lng))
                    },
                    label = { Text("Longitud") },
                    placeholder = { Text("-99.1332") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
            }
        }
    }
}

// Nuevo paso para manejo de imágenes
@Composable
private fun ImagesStep(
    property: Property,
    onPropertyChange: (Property) -> Unit
) {
    var imageUrl by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Imágenes de la propiedad",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Agrega fotos para mostrar mejor tu propiedad",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("URL de imagen") },
                    placeholder = { Text("https://ejemplo.com/imagen.jpg") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )

                Button(
                    onClick = {
                        if (imageUrl.isNotBlank()) {
                            onPropertyChange(property.copy(
                                images = property.images + imageUrl
                            ))
                            imageUrl = ""
                        }
                    },
                    enabled = imageUrl.isNotBlank()
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar")
                }
            }
        }

        // Mostrar imágenes agregadas
        if (property.images.isNotEmpty()) {
            item {
                Text(
                    text = "Imágenes agregadas (${property.images.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium
                )
            }

            items(property.images.size) { index ->
                val image = property.images[index]
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "Imagen ${index + 1}",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyMedium
                        )

                        if (index == 0) {
                            Surface(
                                color = MaterialTheme.colorScheme.primary,
                                shape = MaterialTheme.shapes.small
                            ) {
                                Text(
                                    text = "Principal",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        IconButton(
                            onClick = {
                                onPropertyChange(property.copy(
                                    images = property.images.toMutableList().apply {
                                        removeAt(index)
                                    }
                                ))
                            }
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Eliminar",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        } else {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Sin imágenes agregadas",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

// Mejoré la validación del paso de contacto
@Composable
private fun ContactInfoStep(
    property: Property,
    onPropertyChange: (Property) -> Unit,
    contactError: String?,
    onContactErrorChange: (String?) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Información de contacto",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (contactError != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = contactError,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        item {
            OutlinedTextField(
                value = property.contactInfo.phoneNumber,
                onValueChange = {
                    onPropertyChange(property.copy(
                        contactInfo = property.contactInfo.copy(phoneNumber = it)
                    ))
                    // Validar que al menos un método de contacto esté presente
                    validateContactInfo(property.contactInfo.copy(phoneNumber = it), onContactErrorChange)
                },
                label = { Text("Número de teléfono") },
                placeholder = { Text("+52 123 456 7890") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                leadingIcon = {
                    Icon(Icons.Default.Phone, contentDescription = null)
                }
            )
        }

        item {
            OutlinedTextField(
                value = property.contactInfo.whatsappNumber,
                onValueChange = {
                    onPropertyChange(property.copy(
                        contactInfo = property.contactInfo.copy(whatsappNumber = it)
                    ))
                    validateContactInfo(property.contactInfo.copy(whatsappNumber = it), onContactErrorChange)
                },
                label = { Text("WhatsApp") },
                placeholder = { Text("+52 123 456 7890") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                leadingIcon = {
                    Icon(Icons.Default.Chat, contentDescription = null)
                }
            )
        }

        item {
            OutlinedTextField(
                value = property.contactInfo.email,
                onValueChange = {
                    onPropertyChange(property.copy(
                        contactInfo = property.contactInfo.copy(email = it)
                    ))
                    validateContactInfo(property.contactInfo.copy(email = it), onContactErrorChange)
                },
                label = { Text("Correo electrónico") },
                placeholder = { Text("contacto@ejemplo.com") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null)
                }
            )
        }

        // Resto del contenido del paso de contacto...
        // (mantener el código existente para método preferido y horarios)
    }
}

// Función auxiliar para validar información de contacto
private fun validateContactInfo(contactInfo: ContactInfo, onError: (String?) -> Unit) {
    val hasContact = contactInfo.phoneNumber.isNotBlank() ||
            contactInfo.whatsappNumber.isNotBlank() ||
            contactInfo.email.isNotBlank()

    if (!hasContact) {
        onError("Debe proporcionar al menos un método de contacto")
    } else {
        onError(null)
    }
}

// Validación mejorada con mensajes específicos
private fun validateCurrentStep(step: Int, property: Property): Pair<Boolean, List<ValidationError>> {
    val errors = mutableListOf<ValidationError>()

    when (step) {
        0 -> { // Información básica
            if (property.title.isBlank()) {
                errors.add(ValidationError("title", "El título es obligatorio"))
            } else if (property.title.length > 100) {
                errors.add(ValidationError("title", "El título no puede exceder 100 caracteres"))
            }

            if (property.description.isBlank()) {
                errors.add(ValidationError("description", "La descripción es obligatoria"))
            } else if (property.description.length > 500) {
                errors.add(ValidationError("description", "La descripción no puede exceder 500 caracteres"))
            }

            if (property.address.isBlank()) {
                errors.add(ValidationError("address", "La dirección es obligatoria"))
            }

            if (property.monthlyRent <= 0) {
                errors.add(ValidationError("price", "El precio debe ser mayor a 0"))
            } else if (property.monthlyRent > 50000) {
                errors.add(ValidationError("price", "Precio muy alto, verifica si es correcto"))
            }
        }
        1 -> { // Detalles de propiedad
            if (property.maxOccupants <= 0) {
                errors.add(ValidationError("occupants", "El número de ocupantes debe ser mayor a 0"))
            }
        }
        2 -> { // Servicios y amenidades - opcional
            // Todo es opcional en este paso
        }
        3 -> { // Información de contacto
            val hasContact = property.contactInfo.phoneNumber.isNotBlank() ||
                    property.contactInfo.whatsappNumber.isNotBlank() ||
                    property.contactInfo.email.isNotBlank()

            if (!hasContact) {
                errors.add(ValidationError("contact", "Debe proporcionar al menos un método de contacto"))
            }
        }
        4 -> { // Imágenes - opcional
            // Las imágenes son opcionales
        }
    }

    return Pair(errors.isEmpty(), errors)
}

// Clase para manejar errores de validación
data class ValidationError(
    val field: String,
    val message: String
)

// Función para crear propiedad por defecto
private fun createDefaultProperty(): Property {
    return Property(
        id = "",
        title = "",
        description = "",
        address = "",
        monthlyRent = 0.0,
        deposit = 0.0,
        propertyType = PropertyType.PRIVATE_ROOM.name,
        roomType = RoomType.PRIVATE.name,
        maxOccupants = 1,
        currentOccupants = 0,
        amenities = emptyList(),
        images = emptyList(),
        rules = listOf("No fumar", "No mascotas", "Respetar horarios de silencio"),
        isAvailable = true,
        distanceToUniversity = 0.0,
        utilities = UtilitiesInfo(
            electricityIncluded = false,
            waterIncluded = false,
            internetIncluded = false,
            gasIncluded = false,
            cleaningIncluded = false,
            additionalCosts = 0.0,
            notes = ""
        ),
        contactInfo = ContactInfo(
            phoneNumber = "",
            whatsappNumber = "",
            email = "",
            preferredContactMethod = ContactMethod.WHATSAPP.name,
            contactHours = "9:00 AM - 8:00 PM",
            responseTime = "Menos de 2 horas"
        ),
        latitude = null,
        longitude = null
    )
}

@Composable
private fun StepIndicator(
    title: String,
    stepIndex: Int,
    currentStep: Int
) {
    val isActive = stepIndex == currentStep
    val isCompleted = stepIndex < currentStep

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(32.dp),
            shape = MaterialTheme.shapes.small,
            color = when {
                isCompleted -> MaterialTheme.colorScheme.primary
                isActive -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    Text(
                        text = (stepIndex + 1).toString(),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isActive)
                            MaterialTheme.colorScheme.onPrimaryContainer
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = if (isActive || isCompleted)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PropertyDetailsStep(
    property: Property,
    onPropertyChange: (Property) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Tipo de propiedad",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Column(
                modifier = Modifier.selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PropertyType.values().forEach { type ->
                    PropertyTypeOption(
                        type = type,
                        isSelected = property.propertyType == type.name,
                        onSelected = {
                            onPropertyChange(property.copy(propertyType = type.name))
                        }
                    )
                }
            }
        }

        item {
            Divider()
        }

        item {
            Text(
                text = "Ocupación",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = property.maxOccupants.toString(),
                    onValueChange = {
                        val occupants = it.toIntOrNull() ?: 1
                        onPropertyChange(property.copy(maxOccupants = maxOf(1, occupants)))
                    },
                    label = { Text("Máximo ocupantes") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = {
                        Icon(Icons.Default.People, contentDescription = null)
                    }
                )

                OutlinedTextField(
                    value = property.currentOccupants.toString(),
                    onValueChange = {
                        val current = it.toIntOrNull() ?: 0
                        onPropertyChange(property.copy(currentOccupants = maxOf(0, current)))
                    },
                    label = { Text("Ocupantes actuales") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = null)
                    }
                )
            }
        }

        item {
            OutlinedTextField(
                value = if (property.distanceToUniversity > 0) property.distanceToUniversity.toString() else "",
                onValueChange = {
                    val distance = it.toDoubleOrNull() ?: 0.0
                    onPropertyChange(property.copy(distanceToUniversity = distance))
                },
                label = { Text("Distancia a la universidad (km)") },
                placeholder = { Text("0.0") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                leadingIcon = {
                    Icon(Icons.Default.Directions, contentDescription = null)
                }
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Disponible para renta",
                    style = MaterialTheme.typography.bodyLarge
                )
                Switch(
                    checked = property.isAvailable,
                    onCheckedChange = {
                        onPropertyChange(property.copy(isAvailable = it))
                    }
                )
            }
        }
    }
}

@Composable
private fun PropertyTypeOption(
    type: PropertyType,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    val (icon, title, description) = when (type) {
        PropertyType.HOUSE -> Triple(Icons.Default.Home, "Casa", "Casa completa")
        PropertyType.APARTMENT -> Triple(Icons.Default.Apartment, "Departamento", "Departamento completo")
        PropertyType.PRIVATE_ROOM -> Triple(Icons.Default.Bed, "Cuarto privado", "Habitación privada en casa compartida")
        PropertyType.SHARED_ROOM -> Triple(Icons.Default.Group, "Cuarto compartido", "Habitación compartida con otros")
        PropertyType.STUDIO -> Triple(Icons.Default.SingleBed, "Estudio", "Espacio tipo estudio")
        PropertyType.ENTIRE_APARTMENT -> Triple(Icons.Default.Apartment, "Apartamento completo", "Apartamento completo para ti")
        PropertyType.DORMITORY -> Triple(Icons.Default.School, "Dormitorio", "Dormitorio estudiantil")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onSelected
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected)
            CardDefaults.outlinedCardBorder().copy(
                width = 2.dp,
                brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary)
            )
        else
            CardDefaults.outlinedCardBorder()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSelected)
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            RadioButton(
                selected = isSelected,
                onClick = onSelected,
                colors = RadioButtonDefaults.colors(
                    selectedColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                )
            )
        }
    }
}

@Composable
private fun UtilitiesAndAmenitiesStep(
    property: Property,
    onPropertyChange: (Property) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Servicios incluidos",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            UtilityCheckbox(
                text = "Electricidad",
                icon = Icons.Default.ElectricBolt,
                isChecked = property.utilities.electricityIncluded,
                onCheckedChange = {
                    onPropertyChange(property.copy(
                        utilities = property.utilities.copy(electricityIncluded = it)
                    ))
                }
            )
        }

        item {
            UtilityCheckbox(
                text = "Agua",
                icon = Icons.Default.Water,
                isChecked = property.utilities.waterIncluded,
                onCheckedChange = {
                    onPropertyChange(property.copy(
                        utilities = property.utilities.copy(waterIncluded = it)
                    ))
                }
            )
        }

        item {
            UtilityCheckbox(
                text = "Internet",
                icon = Icons.Default.Wifi,
                isChecked = property.utilities.internetIncluded,
                onCheckedChange = {
                    onPropertyChange(property.copy(
                        utilities = property.utilities.copy(internetIncluded = it)
                    ))
                }
            )
        }

        item {
            UtilityCheckbox(
                text = "Gas",
                icon = Icons.Default.LocalGasStation,
                isChecked = property.utilities.gasIncluded,
                onCheckedChange = {
                    onPropertyChange(property.copy(
                        utilities = property.utilities.copy(gasIncluded = it)
                    ))
                }
            )
        }

        item {
            UtilityCheckbox(
                text = "Servicio de limpieza",
                icon = Icons.Default.CleaningServices,
                isChecked = property.utilities.cleaningIncluded,
                onCheckedChange = {
                    onPropertyChange(property.copy(
                        utilities = property.utilities.copy(cleaningIncluded = it)
                    ))
                }
            )
        }

        item {
            OutlinedTextField(
                value = if (property.utilities.additionalCosts > 0) property.utilities.additionalCosts.toInt().toString() else "",
                onValueChange = {
                    val cost = it.toDoubleOrNull() ?: 0.0
                    onPropertyChange(property.copy(
                        utilities = property.utilities.copy(additionalCosts = cost)
                    ))
                },
                label = { Text("Costos adicionales mensuales") },
                placeholder = { Text("0") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                leadingIcon = {
                    Text("$", modifier = Modifier.padding(start = 12.dp))
                }
            )
        }

        item {
            OutlinedTextField(
                value = property.utilities.notes,
                onValueChange = {
                    onPropertyChange(property.copy(
                        utilities = property.utilities.copy(notes = it)
                    ))
                },
                label = { Text("Notas sobre servicios") },
                placeholder = { Text("Información adicional sobre los servicios...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 3
            )
        }

        item {
            Divider()
        }

        item {
            Text(
                text = "Amenidades",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            AmenityChipSection(
                selectedAmenities = property.amenities,
                onAmenitiesChange = {
                    onPropertyChange(property.copy(amenities = it))
                }
            )
        }

        item {
            Divider()
        }

        item {
            Text(
                text = "Reglas de la casa",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            RulesSection(
                rules = property.rules,
                onRulesChange = {
                    onPropertyChange(property.copy(rules = it))
                }
            )
        }
    }
}

@Composable
private fun UtilityCheckbox(
    text: String,
    icon: ImageVector,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
private fun AmenityChipSection(
    selectedAmenities: List<String>,
    onAmenitiesChange: (List<String>) -> Unit
) {
    val availableAmenities = listOf(
        "Aire acondicionado", "Calefacción", "Balcón", "Terraza",
        "Jardín", "Piscina", "Gimnasio", "Lavandería",
        "Estacionamiento", "Seguridad 24h", "Ascensor", "Amueblado",
        "Cocina equipada", "WiFi", "TV por cable", "Mascotas permitidas"
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(availableAmenities) { amenity ->
            val isSelected = selectedAmenities.contains(amenity)
            FilterChip(
                selected = isSelected,
                onClick = {
                    if (isSelected) {
                        onAmenitiesChange(selectedAmenities - amenity)
                    } else {
                        onAmenitiesChange(selectedAmenities + amenity)
                    }
                },
                label = { Text(amenity) }
            )
        }
    }
}

@Composable
private fun RulesSection(
    rules: List<String>,
    onRulesChange: (List<String>) -> Unit
) {
    var newRule by remember { mutableStateOf("") }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Lista de reglas existentes
        rules.forEach { rule ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CheckCircleOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = rule,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyMedium
                )
                IconButton(
                    onClick = { onRulesChange(rules - rule) }
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar regla",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Campo para agregar nueva regla
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newRule,
                onValueChange = { newRule = it },
                label = { Text("Nueva regla") },
                placeholder = { Text("Ej: No fumar en el interior") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (newRule.isNotBlank()) {
                        onRulesChange(rules + newRule)
                        newRule = ""
                    }
                },
                enabled = newRule.isNotBlank()
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar regla")
            }
        }
    }
}

@Composable
private fun ContactInfoStep(
    property: Property,
    onPropertyChange: (Property) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Información de contacto",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            OutlinedTextField(
                value = property.contactInfo.phoneNumber,
                onValueChange = {
                    onPropertyChange(property.copy(
                        contactInfo = property.contactInfo.copy(phoneNumber = it)
                    ))
                },
                label = { Text("Número de teléfono") },
                placeholder = { Text("+52 123 456 7890") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                leadingIcon = {
                    Icon(Icons.Default.Phone, contentDescription = null)
                }
            )
        }

        item {
            OutlinedTextField(
                value = property.contactInfo.whatsappNumber,
                onValueChange = {
                    onPropertyChange(property.copy(
                        contactInfo = property.contactInfo.copy(whatsappNumber = it)
                    ))
                },
                label = { Text("WhatsApp") },
                placeholder = { Text("+52 123 456 7890") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                leadingIcon = {
                    Icon(Icons.Default.Chat, contentDescription = null)
                }
            )
        }

        item {
            OutlinedTextField(
                value = property.contactInfo.email,
                onValueChange = {
                    onPropertyChange(property.copy(
                        contactInfo = property.contactInfo.copy(email = it)
                    ))
                },
                label = { Text("Correo electrónico") },
                placeholder = { Text("contacto@ejemplo.com") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = null)
                }
            )
        }

        item {
            Text(
                text = "Método de contacto preferido",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )

            Column(
                modifier = Modifier.selectableGroup(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ContactMethod.values().forEach { method ->
                    ContactMethodOption(
                        method = method,
                        isSelected = property.contactInfo.preferredContactMethod == method.name,
                        onSelected = {
                            onPropertyChange(property.copy(
                                contactInfo = property.contactInfo.copy(
                                    preferredContactMethod = method.name
                                )
                            ))
                        }
                    )
                }
            }
        }

        item {
            OutlinedTextField(
                value = property.contactInfo.contactHours,
                onValueChange = {
                    onPropertyChange(property.copy(
                        contactInfo = property.contactInfo.copy(contactHours = it)
                    ))
                },
                label = { Text("Horario de contacto") },
                placeholder = { Text("9:00 AM - 8:00 PM") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                leadingIcon = {
                    Icon(Icons.Default.Schedule, contentDescription = null)
                }
            )
        }

        item {
            OutlinedTextField(
                value = property.contactInfo.responseTime,
                onValueChange = {
                    onPropertyChange(property.copy(
                        contactInfo = property.contactInfo.copy(responseTime = it)
                    ))
                },
                label = { Text("Tiempo de respuesta") },
                placeholder = { Text("Menos de 2 horas") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                leadingIcon = {
                    Icon(Icons.Default.AccessTime, contentDescription = null)
                }
            )
        }
    }
}

@Composable
private fun ContactMethodOption(
    method: ContactMethod,
    isSelected: Boolean,
    onSelected: () -> Unit
) {
    val (icon, title) = when (method) {
        ContactMethod.PHONE -> Icons.Default.Phone to "Teléfono"
        ContactMethod.WHATSAPP -> Icons.Default.Chat to "WhatsApp"
        ContactMethod.EMAIL -> Icons.Default.Email to "Correo electrónico"
        ContactMethod.ANY -> Icons.Default.ContactSupport to "Cualquiera"
        ContactMethod.IN_APP -> Icons.Default.AppShortcut to "En la aplicación"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onSelected
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelected
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun PropertyDialogFooter(
    currentStep: Int,
    totalSteps: Int,
    isLoading: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSave: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 3.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Botón anterior
            if (currentStep > 0) {
                TextButton(
                    onClick = onPrevious,
                    enabled = !isLoading
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Anterior")
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            // Botón siguiente/guardar
            if (currentStep < totalSteps - 1) {
                Button(
                    onClick = onNext,
                    enabled = !isLoading
                ) {
                    Text("Siguiente")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            } else {
                Button(
                    onClick = onSave,
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            Icons.Default.Save,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isLoading) "Guardando..." else "Guardar")
                }
            }
        }
    }
}



