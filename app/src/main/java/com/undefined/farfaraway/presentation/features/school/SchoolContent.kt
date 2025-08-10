package com.undefined.farfaraway.presentation.features.school

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SchoolContent(
    paddingValues: PaddingValues,
    navController: NavController,
    viewModel: SchoolViewModel = hiltViewModel()
) {
    val totalItems by viewModel.totalItems.collectAsState(initial = 0)

    // URLs de ejemplo de imágenes de la universidad (reemplazar con URLs reales)
    val universityImages = listOf(
        "https://static1.educaedu.com.mx/adjuntos/9/00/58/uttt---universidad-tecnol-gica-tula---tepeji-005897_large.jpg", // TODO: Reemplazar con URLs reales
        "https://agendahidalguense.com/wp-content/uploads/2018/02/ofrece-uttt-cursos-de-educacic3b3n-continua.jpg",
        "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEji986WojGG-r_bRKOUY6TyJ2LP4KLeyfBmcopzF_Dz8-nPCu54igPuOx7vqYeBu97ZTIaDDnMnBkoXaIo4Wl49smhMKGvthe3oHFLpNsBGN0DzHvLXG-7TQV8njCpIEG1ovE51_5Fi0V-k/s1600/UT+Tula-Tepeji.jpg",
        "https://oem.com.mx/elsoldehidalgo/img/17568251/1584590481/BASE_LANDSCAPE/480/image.webp",
        "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRbBcirBBxeucSLi1pG4RTFvFgOMFdU23iwpNYtSD468i4rzwqOTnNUeYGYMwdmb5BXGkI&usqp=CAU"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Header con título
        UniversityHeader()

        Spacer(modifier = Modifier.height(24.dp))

        // Carrusel de imágenes
        ImageCarousel(
            images = universityImages,
            modifier = Modifier.height(280.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Información de la universidad
        UniversityInfo()

        Spacer(modifier = Modifier.height(24.dp))

        // Ubicación de la universidad
        LocationSection(
            onOpenMapsClick = {
                // TODO: Abrir Google Maps con la ubicación de la universidad
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Información de contacto
        ContactSection()

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun UniversityHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Outlined.School,
            contentDescription = "Universidad",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Universidad Tecnológica",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Tula-Tepeji",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageCarousel(
    images: List<String>,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { images.size })

    // Auto-scroll del carrusel
    LaunchedEffect(pagerState) {
        while (true) {
            delay(4000) // Cambia cada 4 segundos
            val nextPage = (pagerState.currentPage + 1) % images.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue
                            alpha = lerp(
                                start = 0.5f,
                                stop = 1.0f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                            scaleX = lerp(
                                start = 0.9f,
                                stop = 1.0f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                            scaleY = lerp(
                                start = 0.9f,
                                stop = 1.0f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                        }
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    // Placeholder mientras no tengamos las imágenes reales
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Photo,
                                contentDescription = "Imagen ${page + 1}",
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Imagen ${page + 1}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                        }
                    }

                    // Descomenta esto cuando tengas las URLs reales de las imágenes
                    /*
                    AsyncImage(
                        model = images[page],
                        contentDescription = "Imagen ${page + 1} de la universidad",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    */
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Indicadores del pager
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(images.size) { index ->
                Box(
                    modifier = Modifier
                        .size(if (pagerState.currentPage == index) 12.dp else 8.dp)
                        .clip(CircleShape)
                        .background(
                            if (pagerState.currentPage == index)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                        .animateContentSize()
                )
                if (index != images.lastIndex) {
                    Spacer(modifier = Modifier.width(6.dp))
                }
            }
        }
    }
}

@Composable
fun UniversityInfo() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Acerca de la Universidad",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(12.dp)
                )
                .padding(20.dp)
        ) {
            Text(
                text = "La Universidad Tecnológica Tula-Tepeji es una institución de educación superior pública que ofrece programas académicos de calidad en diversas áreas del conocimiento, formando profesionistas competitivos y comprometidos con el desarrollo regional.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2
            )

            Spacer(modifier = Modifier.height(16.dp))

            InfoItem(
                icon = Icons.Outlined.CalendarToday,
                title = "Fundada en",
                value = "1991"
            )

            InfoItem(
                icon = Icons.Outlined.People,
                title = "Estudiantes",
                value = "2,500+"
            )

            InfoItem(
                icon = Icons.Outlined.MenuBook,
                title = "Programas académicos",
                value = "15+"
            )
        }
    }
}

@Composable
fun InfoItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun LocationSection(
    onOpenMapsClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Ubicación",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(12.dp)
                )
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = "Ubicación",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Dirección",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "Av. Universidad Tecnológica #1000, El Carmen, Tula de Allende, Hidalgo, México",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.2
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            FilledTonalButton(
                onClick = onOpenMapsClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.Map,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ver en Google Maps")
            }
        }
    }
}

@Composable
fun ContactSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Contacto",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(12.dp)
                )
                .padding(20.dp)
        ) {
            ContactItem(
                icon = Icons.Outlined.Phone,
                title = "Teléfono",
                value = "+52 773 732 9000"
            )

            ContactItem(
                icon = Icons.Outlined.Email,
                title = "Email",
                value = "informes@uttt.edu.mx"
            )

            ContactItem(
                icon = Icons.Outlined.Language,
                title = "Sitio web",
                value = "www.uttt.edu.mx"
            )
        }
    }
}

@Composable
fun ContactItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}