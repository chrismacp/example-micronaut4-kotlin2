package dev.macpherson.example.micronaut4kotlin2.api.dto

import dev.macpherson.example.micronaut4kotlin2.domain.model.Planet
import io.micronaut.serde.annotation.Serdeable
import java.util.UUID

@Serdeable
data class PlanetResponse(
    val id: UUID,
    val name: String,
    val description: String?
) {
    companion object {
        fun from(planet: Planet): PlanetResponse {
            return PlanetResponse(
                id = planet.id,
                name = planet.name,
                description = planet.description
            )
        }
    }
}