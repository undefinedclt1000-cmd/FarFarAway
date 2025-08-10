package com.undefined.farfaraway.presentation.features.routes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.undefined.farfaraway.domain.entities.TransportRoute
import com.undefined.farfaraway.domain.entities.Location
import com.undefined.farfaraway.domain.entities.TransportType
import com.undefined.farfaraway.domain.entities.ScheduleTime
import com.undefined.farfaraway.domain.entities.DayOfWeek
import com.undefined.farfaraway.domain.entities.TransportStop

@HiltViewModel
class RoutesViewModel @Inject constructor(
    // Aquí inyectarías tu repositorio de rutas
    // private val routesRepository: RoutesRepository
): ViewModel() {

    private val _routes = MutableStateFlow<List<TransportRoute>>(emptyList())
    val routes: StateFlow<List<TransportRoute>> = _routes.asStateFlow()

    private val _selectedRoute = MutableStateFlow<TransportRoute?>(null)
    val selectedRoute: StateFlow<TransportRoute?> = _selectedRoute.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _totalItems = MutableStateFlow(0)
    val totalItems: StateFlow<Int> = _totalItems

    init {
        loadRoutes()
    }

    fun loadRoutes() {
        viewModelScope.launch {
            _isLoading.value = true

            // Datos de ejemplo - aquí harías la llamada a tu repositorio
            val sampleRoutes = getSampleRoutes()

            _routes.value = sampleRoutes
            _totalItems.value = sampleRoutes.size
            _isLoading.value = false
        }
    }

    fun selectRoute(route: TransportRoute) {
        _selectedRoute.value = route
    }

    fun clearSelection() {
        _selectedRoute.value = null
    }

    fun refreshRoutes() {
        loadRoutes()
    }

    private fun getSampleRoutes(): List<TransportRoute> {
        return listOf(
            // Ruta principal: Tula de Allende - Tepeji del Río (vía UTTT)
            TransportRoute(
                id = "1",
                routeName = "Central Tula - Central Tepeji del Río (vía UTTT)",
                routeNumber = "Tula-Tepeji",
                origin = Location(
                    latitude = 20.0535,
                    longitude = -99.3396,
                    address = "Central de Autobuses Tula de Allende",
                    name = "Central Tula"
                ),
                destination = Location(
                    latitude = 19.9118,
                    longitude = -99.3440,
                    address = "Central de Autobuses Tepeji del Río",
                    name = "Central Tepeji"
                ),
                stops = listOf(
                    TransportStop(
                        id = "stop1",
                        name = "UTTT (Universidad Tecnológica Tula-Tepeji)",
                        location = Location(
                            latitude = 19.9800,
                            longitude = -99.3420,
                            address = "Carretera Tula-Tepeji Km 8",
                            name = "UTTT"
                        ),
                        estimatedTime = 15,
                        isMainStop = true
                    ),
                    TransportStop(
                        id = "stop2",
                        name = "Crucero El Llano",
                        location = Location(
                            latitude = 19.9650,
                            longitude = -99.3430,
                            address = "Crucero El Llano",
                            name = "El Llano"
                        ),
                        estimatedTime = 20,
                        isMainStop = false
                    ),
                    TransportStop(
                        id = "stop3",
                        name = "Santa Ana Ahuehuepan",
                        location = Location(
                            latitude = 19.9400,
                            longitude = -99.3435,
                            address = "Santa Ana Ahuehuepan",
                            name = "Santa Ana"
                        ),
                        estimatedTime = 30,
                        isMainStop = false
                    )
                ),
                schedule = generateScheduleEvery15Minutes("1", "06:00", "21:30"),
                fare = 18.0,
                estimatedDuration = 35,
                transportType = TransportType.MICRO.name,
                isActive = true
            ),

            // Ruta: Tula de Allende - San Ildefonso
            TransportRoute(
                id = "2",
                routeName = "Tula de Allende - San Ildefonso",
                routeNumber = "Tula-San Ildefonso",
                origin = Location(
                    latitude = 20.0535,
                    longitude = -99.3396,
                    address = "Centro de Tula de Allende",
                    name = "Centro Tula"
                ),
                destination = Location(
                    latitude = 20.1200,
                    longitude = -99.2800,
                    address = "San Ildefonso, Hidalgo",
                    name = "San Ildefonso"
                ),
                stops = listOf(
                    TransportStop(
                        id = "stop4",
                        name = "Hospital General Tula",
                        location = Location(
                            latitude = 20.0580,
                            longitude = -99.3350,
                            address = "Hospital General Tula",
                            name = "Hospital General"
                        ),
                        estimatedTime = 5,
                        isMainStop = true
                    ),
                    TransportStop(
                        id = "stop5",
                        name = "Crucero San Ildefonso",
                        location = Location(
                            latitude = 20.0900,
                            longitude = -99.3100,
                            address = "Crucero San Ildefonso",
                            name = "Crucero"
                        ),
                        estimatedTime = 15,
                        isMainStop = false
                    ),
                    TransportStop(
                        id = "stop6",
                        name = "Pueblo San Ildefonso Centro",
                        location = Location(
                            latitude = 20.1150,
                            longitude = -99.2850,
                            address = "Centro de San Ildefonso",
                            name = "Centro San Ildefonso"
                        ),
                        estimatedTime = 20,
                        isMainStop = true
                    )
                ),
                schedule = generateScheduleEvery30Minutes("2", "06:00", "20:00"),
                fare = 15.0,
                estimatedDuration = 25,
                transportType = TransportType.MICRO.name,
                isActive = true
            ),

            // Ruta: Tula de Allende - Ciudad Cooperativa Cruz Azul
            TransportRoute(
                id = "3",
                routeName = "Tula de Allende - Ciudad Cooperativa Cruz Azul",
                routeNumber = "Tula-Cruz Azul",
                origin = Location(
                    latitude = 20.0535,
                    longitude = -99.3396,
                    address = "Centro de Tula de Allende",
                    name = "Centro Tula"
                ),
                destination = Location(
                    latitude = 20.0100,
                    longitude = -99.4200,
                    address = "Ciudad Cooperativa Cruz Azul Centro",
                    name = "Cruz Azul"
                ),
                stops = listOf(
                    TransportStop(
                        id = "stop7",
                        name = "IMSS Tula",
                        location = Location(
                            latitude = 20.0520,
                            longitude = -99.3450,
                            address = "IMSS Tula de Allende",
                            name = "IMSS Tula"
                        ),
                        estimatedTime = 5,
                        isMainStop = true
                    ),
                    TransportStop(
                        id = "stop8",
                        name = "Jasso",
                        location = Location(
                            latitude = 20.0300,
                            longitude = -99.3800,
                            address = "Jasso, Tula de Allende",
                            name = "Jasso"
                        ),
                        estimatedTime = 15,
                        isMainStop = false
                    ),
                    TransportStop(
                        id = "stop9",
                        name = "Entrada Cooperativa",
                        location = Location(
                            latitude = 20.0200,
                            longitude = -99.4000,
                            address = "Entrada Ciudad Cooperativa",
                            name = "Entrada Cooperativa"
                        ),
                        estimatedTime = 25,
                        isMainStop = false
                    ),
                    TransportStop(
                        id = "stop10",
                        name = "Cruz Azul Centro",
                        location = Location(
                            latitude = 20.0120,
                            longitude = -99.4180,
                            address = "Centro Ciudad Cooperativa Cruz Azul",
                            name = "Centro Cruz Azul"
                        ),
                        estimatedTime = 30,
                        isMainStop = true
                    )
                ),
                schedule = generateScheduleEvery20Minutes("3", "05:30", "21:00"),
                fare = 22.0,
                estimatedDuration = 35,
                transportType = TransportType.BUS.name,
                isActive = true
            ),

            // Ruta: Tepeji del Río - Tula de Allende (Ruta de regreso directa)
            TransportRoute(
                id = "4",
                routeName = "Tepeji del Río - Tula de Allende (directo)",
                routeNumber = "Tepeji-Tula",
                origin = Location(
                    latitude = 19.9118,
                    longitude = -99.3440,
                    address = "Central de Autobuses Tepeji del Río",
                    name = "Central Tepeji"
                ),
                destination = Location(
                    latitude = 20.0535,
                    longitude = -99.3396,
                    address = "Central de Autobuses Tula de Allende",
                    name = "Central Tula"
                ),
                stops = listOf(
                    TransportStop(
                        id = "stop11",
                        name = "Plaza Tepeji",
                        location = Location(
                            latitude = 19.9130,
                            longitude = -99.3420,
                            address = "Plaza Principal Tepeji del Río",
                            name = "Plaza Tepeji"
                        ),
                        estimatedTime = 3,
                        isMainStop = true
                    ),
                    TransportStop(
                        id = "stop12",
                        name = "UTTT (Universidad Tecnológica)",
                        location = Location(
                            latitude = 19.9800,
                            longitude = -99.3420,
                            address = "Universidad Tecnológica Tula-Tepeji",
                            name = "UTTT"
                        ),
                        estimatedTime = 20,
                        isMainStop = true
                    )
                ),
                schedule = generateScheduleEvery15Minutes("4", "06:15", "21:45"),
                fare = 18.0,
                estimatedDuration = 35,
                transportType = TransportType.MICRO.name,
                isActive = true
            ),

            // Ruta urbana: Tula Centro - Colonias Norte
            TransportRoute(
                id = "5",
                routeName = "Tula Centro - Colonia Ampliación Pueblo Nuevo",
                routeNumber = "Urbano Norte",
                origin = Location(
                    latitude = 20.0535,
                    longitude = -99.3396,
                    address = "Centro de Tula de Allende",
                    name = "Centro Tula"
                ),
                destination = Location(
                    latitude = 20.0700,
                    longitude = -99.3200,
                    address = "Colonia Ampliación Pueblo Nuevo",
                    name = "Pueblo Nuevo"
                ),
                stops = listOf(
                    TransportStop(
                        id = "stop13",
                        name = "Mercado Municipal",
                        location = Location(
                            latitude = 20.0545,
                            longitude = -99.3380,
                            address = "Mercado Municipal Tula",
                            name = "Mercado"
                        ),
                        estimatedTime = 3,
                        isMainStop = true
                    ),
                    TransportStop(
                        id = "stop14",
                        name = "Preparatoria CBTA",
                        location = Location(
                            latitude = 20.0620,
                            longitude = -99.3300,
                            address = "CBTA Tula",
                            name = "CBTA"
                        ),
                        estimatedTime = 8,
                        isMainStop = true
                    )
                ),
                schedule = generateScheduleEvery10Minutes("5", "06:00", "21:00"),
                fare = 10.0,
                estimatedDuration = 15,
                transportType = TransportType.MICRO.name,
                isActive = true
            ),

            // Ruta especial: Tula - Refinería (para trabajadores)
            TransportRoute(
                id = "6",
                routeName = "Tula Centro - Refinería Miguel Hidalgo",
                routeNumber = "Tula-Refinería",
                origin = Location(
                    latitude = 20.0535,
                    longitude = -99.3396,
                    address = "Centro de Tula de Allende",
                    name = "Centro Tula"
                ),
                destination = Location(
                    latitude = 20.0800,
                    longitude = -99.3600,
                    address = "Refinería Miguel Hidalgo",
                    name = "Refinería"
                ),
                stops = listOf(
                    TransportStop(
                        id = "stop15",
                        name = "Entrada Principal Refinería",
                        location = Location(
                            latitude = 20.0750,
                            longitude = -99.3550,
                            address = "Acceso Principal Refinería",
                            name = "Entrada Refinería"
                        ),
                        estimatedTime = 12,
                        isMainStop = true
                    )
                ),
                schedule = generateScheduleForWorkers("6"), // Horarios especiales para trabajadores
                fare = 12.0,
                estimatedDuration = 15,
                transportType = TransportType.BUS.name,
                isActive = true
            )
        )
    }

    // Función auxiliar para generar horarios cada 30 minutos
    private fun generateScheduleEvery30Minutes(
        routeId: String,
        startTime: String,
        endTime: String
    ): List<ScheduleTime> {
        val schedules = mutableListOf<ScheduleTime>()
        val startHour = startTime.split(":")[0].toInt()
        val startMinute = startTime.split(":")[1].toInt()
        val endHour = endTime.split(":")[0].toInt()
        val endMinute = endTime.split(":")[1].toInt()

        var currentHour = startHour
        var currentMinute = startMinute
        var id = 1

        while (currentHour < endHour || (currentHour == endHour && currentMinute <= endMinute)) {
            val timeString = String.format("%02d:%02d", currentHour, currentMinute)

            DayOfWeek.values().forEach { day ->
                schedules.add(
                    ScheduleTime(
                        id = "${routeId}_${day.name}_${id}",
                        routeId = routeId,
                        departureTime = timeString,
                        dayOfWeek = day.name,
                        isWeekend = day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY
                    )
                )
            }

            currentMinute += 30
            if (currentMinute >= 60) {
                currentMinute = 0
                currentHour++
            }
            id++
        }

        return schedules
    }

    // Función auxiliar para generar horarios cada 20 minutos
    private fun generateScheduleEvery20Minutes(
        routeId: String,
        startTime: String,
        endTime: String
    ): List<ScheduleTime> {
        val schedules = mutableListOf<ScheduleTime>()
        val startHour = startTime.split(":")[0].toInt()
        val startMinute = startTime.split(":")[1].toInt()
        val endHour = endTime.split(":")[0].toInt()
        val endMinute = endTime.split(":")[1].toInt()

        var currentHour = startHour
        var currentMinute = startMinute
        var id = 1

        while (currentHour < endHour || (currentHour == endHour && currentMinute <= endMinute)) {
            val timeString = String.format("%02d:%02d", currentHour, currentMinute)

            DayOfWeek.values().forEach { day ->
                schedules.add(
                    ScheduleTime(
                        id = "${routeId}_${day.name}_${id}",
                        routeId = routeId,
                        departureTime = timeString,
                        dayOfWeek = day.name,
                        isWeekend = day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY
                    )
                )
            }

            currentMinute += 20
            if (currentMinute >= 60) {
                currentMinute -= 60
                currentHour++
            }
            id++
        }

        return schedules
    }

    // Función auxiliar para generar horarios cada 10 minutos
    private fun generateScheduleEvery10Minutes(
        routeId: String,
        startTime: String,
        endTime: String
    ): List<ScheduleTime> {
        val schedules = mutableListOf<ScheduleTime>()
        val startHour = startTime.split(":")[0].toInt()
        val startMinute = startTime.split(":")[1].toInt()
        val endHour = endTime.split(":")[0].toInt()
        val endMinute = endTime.split(":")[1].toInt()

        var currentHour = startHour
        var currentMinute = startMinute
        var id = 1

        while (currentHour < endHour || (currentHour == endHour && currentMinute <= endMinute)) {
            val timeString = String.format("%02d:%02d", currentHour, currentMinute)

            DayOfWeek.values().forEach { day ->
                if (day != DayOfWeek.SUNDAY) { // Urbanas no corren domingos
                    schedules.add(
                        ScheduleTime(
                            id = "${routeId}_${day.name}_${id}",
                            routeId = routeId,
                            departureTime = timeString,
                            dayOfWeek = day.name,
                            isWeekend = day == DayOfWeek.SATURDAY
                        )
                    )
                }
            }

            currentMinute += 10
            if (currentMinute >= 60) {
                currentMinute = 0
                currentHour++
            }
            id++
        }

        return schedules
    }

    // Función auxiliar para generar horarios especiales para trabajadores
    private fun generateScheduleForWorkers(routeId: String): List<ScheduleTime> {
        val schedules = mutableListOf<ScheduleTime>()
        val workerTimes = listOf(
            "05:30", "06:00", "06:30", "07:00", "07:30", // Entrada turno matutino
            "14:00", "14:30", "15:00", "15:30", // Salida turno matutino / Entrada vespertino
            "22:00", "22:30", "23:00" // Salida turno nocturno
        )

        var id = 1
        DayOfWeek.values().forEach { day ->
            if (day != DayOfWeek.SUNDAY) { // La refinería no opera domingos
                workerTimes.forEach { time ->
                    schedules.add(
                        ScheduleTime(
                            id = "${routeId}_${day.name}_${id}",
                            routeId = routeId,
                            departureTime = time,
                            dayOfWeek = day.name,
                            isWeekend = day == DayOfWeek.SATURDAY
                        )
                    )
                    id++
                }
            }
        }

        return schedules
    }
    // Función auxiliar para generar horarios cada 15 minutos
    private fun generateScheduleEvery15Minutes(
        routeId: String,
        startTime: String,
        endTime: String
    ): List<ScheduleTime> {
        val schedules = mutableListOf<ScheduleTime>()
        val startHour = startTime.split(":")[0].toInt()
        val startMinute = startTime.split(":")[1].toInt()
        val endHour = endTime.split(":")[0].toInt()
        val endMinute = endTime.split(":")[1].toInt()

        var currentHour = startHour
        var currentMinute = startMinute
        var id = 1

        while (currentHour < endHour || (currentHour == endHour && currentMinute <= endMinute)) {
            val timeString = String.format("%02d:%02d", currentHour, currentMinute)

            // Agregar para todos los días de la semana
            DayOfWeek.values().forEach { day ->
                schedules.add(
                    ScheduleTime(
                        id = "${routeId}_${day.name}_${id}",
                        routeId = routeId,
                        departureTime = timeString,
                        dayOfWeek = day.name,
                        isWeekend = day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY
                    )
                )
            }

            currentMinute += 15
            if (currentMinute >= 60) {
                currentMinute = 0
                currentHour++
            }
            id++
        }

        return schedules
    }

    // Función auxiliar para generar horarios cada 25 minutos
    private fun generateScheduleEvery25Minutes(
        routeId: String,
        startTime: String,
        endTime: String
    ): List<ScheduleTime> {
        val schedules = mutableListOf<ScheduleTime>()
        val startHour = startTime.split(":")[0].toInt()
        val startMinute = startTime.split(":")[1].toInt()
        val endHour = endTime.split(":")[0].toInt()
        val endMinute = endTime.split(":")[1].toInt()

        var currentHour = startHour
        var currentMinute = startMinute
        var id = 1

        while (currentHour < endHour || (currentHour == endHour && currentMinute <= endMinute)) {
            val timeString = String.format("%02d:%02d", currentHour, currentMinute)

            DayOfWeek.values().forEach { day ->
                schedules.add(
                    ScheduleTime(
                        id = "${routeId}_${day.name}_${id}",
                        routeId = routeId,
                        departureTime = timeString,
                        dayOfWeek = day.name,
                        isWeekend = day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY
                    )
                )
            }

            currentMinute += 25
            if (currentMinute >= 60) {
                currentMinute -= 60
                currentHour++
            }
            id++
        }

        return schedules
    }

    // Función auxiliar para generar horarios cada 45 minutos
    private fun generateScheduleEvery45Minutes(
        routeId: String,
        startTime: String,
        endTime: String
    ): List<ScheduleTime> {
        val schedules = mutableListOf<ScheduleTime>()
        val startHour = startTime.split(":")[0].toInt()
        val startMinute = startTime.split(":")[1].toInt()
        val endHour = endTime.split(":")[0].toInt()
        val endMinute = endTime.split(":")[1].toInt()

        var currentHour = startHour
        var currentMinute = startMinute
        var id = 1

        while (currentHour < endHour || (currentHour == endHour && currentMinute <= endMinute)) {
            val timeString = String.format("%02d:%02d", currentHour, currentMinute)

            DayOfWeek.values().forEach { day ->
                schedules.add(
                    ScheduleTime(
                        id = "${routeId}_${day.name}_${id}",
                        routeId = routeId,
                        departureTime = timeString,
                        dayOfWeek = day.name,
                        isWeekend = day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY
                    )
                )
            }

            currentMinute += 45
            if (currentMinute >= 60) {
                currentMinute -= 60
                currentHour++
            }
            id++
        }

        return schedules
    }
}