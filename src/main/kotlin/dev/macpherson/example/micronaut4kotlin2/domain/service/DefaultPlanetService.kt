// src/main/kotlin/dev/macpherson/domain/service/DefaultPlanetService.kt
package dev.macpherson.example.micronaut4kotlin2.domain.service

import dev.macpherson.example.micronaut4kotlin2.domain.model.Planet
import dev.macpherson.example.micronaut4kotlin2.domain.repository.PlanetRepository
import jakarta.inject.Singleton
import java.util.UUID

@Singleton
class DefaultPlanetService(
    private val planetRepository: PlanetRepository
) : PlanetService {

    override fun createPlanet(name: String, description: String?): Planet {
        require(name.isNotBlank()) { "Planet name cannot be blank" }
        require(name.length <= 100) { "Planet name cannot be longer than 100 characters" }

        // Check if planet with this name already exists (case-insensitive)
        planetRepository.findByName(name)?.let {
            throw IllegalArgumentException("A planet with name '$name' already exists")
        }

        return try {
            val planet = Planet.create(name, description)
            planetRepository.save(planet)
        } catch (e: IllegalArgumentException) {
            // Re-throw with more context if needed
            throw IllegalArgumentException("Failed to create planet: ${e.message}", e)
        }
    }

    override fun getPlanet(id: UUID): Planet? {
        return planetRepository.findById(id)
    }

    override fun getPlanetByName(name: String): Planet? {
        return planetRepository.findByName(name)
    }

    override fun getAllPlanets(): List<Planet> {
        return planetRepository.findAll()
    }

    override fun updatePlanet(id: UUID, name: String, description: String?): Planet {
        val existing = planetRepository.findById(id) ?:
        throw NoSuchElementException("No planet found with id: $id")

        // Check if another planet with the new name already exists
        if (name != existing.name) {
            planetRepository.findByName(name)?.let {
                throw IllegalArgumentException("A planet with name '$name' already exists")
            }
        }

        val updated = existing.copy(name = name, description = description)
        return planetRepository.update(updated)
    }

    override fun deletePlanet(id: UUID): Boolean {
        return planetRepository.delete(id)
    }
}