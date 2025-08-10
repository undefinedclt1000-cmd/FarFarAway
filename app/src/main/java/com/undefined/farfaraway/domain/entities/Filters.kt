package com.undefined.farfaraway.domain.entities

import com.google.firebase.database.PropertyName


data class SearchFilters(
    @get:PropertyName("query") val query: String = "",
    @get:PropertyName("property_type") val propertyType: String = "",
    @get:PropertyName("min_price") val minPrice: Double = 0.0,
    @get:PropertyName("max_price") val maxPrice: Double = 0.0,
    @get:PropertyName("max_distance") val maxDistance: Double = 0.0,
    @get:PropertyName("amenities") val amenities: List<String> = emptyList(),
    @get:PropertyName("max_occupants") val maxOccupants: Int = 0,
    @get:PropertyName("sort_by") val sortBy: String = SortBy.RELEVANCE.name,
    @get:PropertyName("sort_order") val sortOrder: String = SortOrder.DESC.name
) {
    constructor() : this("", "", 0.0, 0.0, 0.0, emptyList(), 0, SortBy.RELEVANCE.name, SortOrder.DESC.name)
}

enum class SortBy {
    RELEVANCE,      // Relevancia
    PRICE,          // Precio
    DISTANCE,       // Distancia
    RATING,         // Calificación
    CREATED_DATE    // Fecha de creación
}

enum class SortOrder {
    ASC,            // Ascendente
    DESC            // Descendente
}
