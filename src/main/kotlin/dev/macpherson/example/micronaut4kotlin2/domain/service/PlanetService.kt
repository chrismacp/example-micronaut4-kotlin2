// src/main/kotlin/dev/macpherson/example/domain/service/PlanetService.kt
package dev.macpherson.example.micronaut4kotlin2.domain.service

import dev.macpherson.example.micronaut4kotlin2.domain.model.Planet
import java.util.UUID

/**
 * Service for managing planets in the system.
 */
interface PlanetService {
    /**
     * Creates a new planet with the given name and optional description.
     * @throws IllegalArgumentException if a planet with the same name already exists
     */
    fun createPlanet(name: String, description: String? = null): Planet

    /**
     * Retrieves a planet by its unique identifier.
     * @return the planet if found, null otherwise
     */
    fun getPlanet(id: UUID): Planet?

    /**
     * Retrieves a planet by its name (case-insensitive).
     * @return the planet if found, null otherwise
     */
    fun getPlanetByName(name: String): Planet?

    /**
     * Retrieves all planets in the system.
     * @return a list of all planets, or an empty list if none exist
     */
    fun getAllPlanets(): List<Planet>

    /**
     * Updates an existing planet's details.
     * @throws NoSuchElementException if no planet exists with the given ID
     */
    fun updatePlanet(id: UUID, name: String, description: String?): Planet

    /**
     * Deletes a planet by its unique identifier.
     * @return true if a planet was deleted, false if no planet was found
     */
    fun deletePlanet(id: UUID): Boolean
}