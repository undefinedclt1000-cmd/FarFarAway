package com.undefined.farfaraway.domain.entities

import com.google.firebase.database.PropertyName
import java.util.Date

data class TransportRoute(
    @get:PropertyName("id") val id: String = "",
    @get:PropertyName("route_name") val routeName: String = "",
    @get:PropertyName("route_number") val routeNumber: String = "",
    @get:PropertyName("origin") val origin: Location = Location(),
    @get:PropertyName("destination") val destination: Location = Location(),
    @get:PropertyName("stops") val stops: List<TransportStop> = emptyList(),
    @get:PropertyName("schedule") val schedule: List<ScheduleTime> = emptyList(),
    @get:PropertyName("fare") val fare: Double = 0.0,
    @get:PropertyName("estimated_duration") val estimatedDuration: Int = 0,
    @get:PropertyName("transport_type") val transportType: String = TransportType.BUS.name,
    @get:PropertyName("is_active") val isActive: Boolean = true,
) {
    constructor() : this("", "", "", Location(), Location(), emptyList(), emptyList(),
        0.0, 0, TransportType.BUS.name, true)
}

data class TransportStop(
    @get:PropertyName("id") val id: String = "",
    @get:PropertyName("name") val name: String = "",
    @get:PropertyName("location") val location: Location = Location(),
    @get:PropertyName("estimated_time") val estimatedTime: Int = 0,
    @get:PropertyName("is_main_stop") val isMainStop: Boolean = false
) {
    constructor() : this("", "", Location(), 0, false)
}

data class ScheduleTime(
    @get:PropertyName("id") val id: String = "",
    @get:PropertyName("route_id") val routeId: String = "",
    @get:PropertyName("departure_time") val departureTime: String = "",
    @get:PropertyName("day_of_week") val dayOfWeek: String = DayOfWeek.MONDAY.name,
    @get:PropertyName("is_weekend") val isWeekend: Boolean = false
) {
    constructor() : this("", "", "", DayOfWeek.MONDAY.name, false)
}

data class Location(
    @get:PropertyName("latitude") val latitude: Double = 0.0,
    @get:PropertyName("longitude") val longitude: Double = 0.0,
    @get:PropertyName("address") val address: String = "",
    @get:PropertyName("name") val name: String = ""
) {
    constructor() : this(0.0, 0.0, "", "")
}

enum class TransportType {
    BUS,            // Camión
    MICRO,          // Micro
    SHARED_TAXI,    // Taxi colectivo
    METRO,          // Metro
    OTHER           // Otro
}

enum class DayOfWeek {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY
}
