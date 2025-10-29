package dev.macpherson.example.micronaut4kotlin2.infra.repository

import dev.macpherson.example.micronaut4kotlin2.domain.model.Planet
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class InMemoryPlanetRepositoryTest : StringSpec({
    val repository = InMemoryPlanetRepository()

    beforeEach {
        repository.clear()
    }

    "save should generate an ID if not provided" {
        // Given
        val planet = Planet.create(name = "Earth", description = "Blue planet")

        // When
        val saved = repository.save(planet)

        // Then
        saved.id shouldNotBe null
        saved.name shouldBe "Earth"
        saved.description shouldBe "Blue planet"
    }

    "findById should return the correct planet" {
        // Given
        val planet = repository.save(Planet.create(name = "Mars", description = "Red planet"))

        // When
        val found = repository.findById(planet.id)

        // Then
        found shouldNotBe null
        found?.name shouldBe "Mars"
        found?.description shouldBe "Red planet"
    }

    "findByName should be case insensitive" {
        // Given
        repository.save(Planet.create(name = "Jupiter", description = "Gas giant"))

        // When
        val found = repository.findByName("jupiter")

        // Then
        found shouldNotBe null
        found?.name shouldBe "Jupiter"
    }

    "update should update existing planet" {
        // Given
        val planet = repository.save(Planet.create(name = "Venus", description = "Morning star"))
        val updated = planet.copy(description = "Evening star")

        // When
        val result = repository.update(updated)

        // Then
        result.description shouldBe "Evening star"
        val found = repository.findById(planet.id)
        found?.description shouldBe "Evening star"
    }

    "delete should remove the planet" {
        // Given
        val planet = repository.save(Planet.create(name = "Mercury", description = "Closest to sun"))

        // When
        val deleted = repository.delete(planet.id)

        // Then
        deleted shouldBe true
        repository.findById(planet.id) shouldBe null
    }

    "findAll should return all planets" {
        // Given
        repository.save(Planet.create(name = "Saturn", description = "Has rings"))
        repository.save(Planet.create(name = "Uranus", description = "Tilted planet"))

        // When
        val allPlanets = repository.findAll()

        // Then
        allPlanets.size shouldBe 2
        allPlanets.map { it.name }.toSet() shouldBe setOf("Saturn", "Uranus")
    }

    "findAll should return empty list when no planets exist" {
        // Given - no planets added

        // When
        val allPlanets = repository.findAll()

        // Then
        allPlanets shouldBe emptyList()
    }

    "findById should return null for non-existent planet" {
        // Given
        val nonExistentId = java.util.UUID.randomUUID()

        // When
        val found = repository.findById(nonExistentId)

        // Then
        found shouldBe null
    }

    "findByName should return null for non-existent planet" {
        // Given - no planets added

        // When
        val found = repository.findByName("NonExistent")

        // Then
        found shouldBe null
    }
})