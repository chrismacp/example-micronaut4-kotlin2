package dev.macpherson.example.micronaut4kotlin2.domain.service

import dev.macpherson.example.micronaut4kotlin2.infra.repository.InMemoryPlanetRepository
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import java.util.UUID
import kotlin.collections.map

class DefaultPlanetServiceTest : StringSpec({
    val repository = InMemoryPlanetRepository()
    val service = DefaultPlanetService(repository)

    beforeEach {
        repository.clear()
    }

    "createPlanet should create a new planet" {
        // When
        val planet = service.createPlanet("Earth", "Blue planet")

        // Then
        planet.name shouldBe "Earth"
        planet.description shouldBe "Blue planet"
        planet.id shouldNotBe null
    }

    "createPlanet should throw when planet with same name exists" {
        // Given
        service.createPlanet("Mars")

        // When
        val exception = runCatching {
            service.createPlanet("Mars")
        }.exceptionOrNull()

        // Then
        exception.shouldBeInstanceOf<IllegalArgumentException>()
        exception?.message shouldBe "A planet with name 'Mars' already exists"
    }

    "getPlanet should return planet by id" {
        // Given
        val saved = service.createPlanet("Jupiter")

        // When
        val found = service.getPlanet(saved.id)

        // Then
        found shouldNotBe null
        found?.name shouldBe "Jupiter"
    }

    "getPlanet should return null for non-existent id" {
        // When
        val found = service.getPlanet(UUID.randomUUID())

        // Then
        found shouldBe null
    }

    "updatePlanet should update existing planet" {
        // Given
        val planet = service.createPlanet("Venus", "Morning star")

        // When
        val updated = service.updatePlanet(planet.id, "Venus", "Evening star")

        // Then
        updated.description shouldBe "Evening star"
        service.getPlanet(planet.id)?.description shouldBe "Evening star"
    }

    "updatePlanet should throw when planet not found" {
        // When
        val exception = runCatching {
            service.updatePlanet(UUID.randomUUID(), "Nonexistent", "Test")
        }.exceptionOrNull()

        // Then
        exception.shouldBeInstanceOf<NoSuchElementException>()
    }

    "deletePlanet should remove the planet" {
        // Given
        val planet = service.createPlanet("Mercury")

        // When
        val result = service.deletePlanet(planet.id)

        // Then
        result shouldBe true
        service.getPlanet(planet.id) shouldBe null
    }

    "deletePlanet should return false for non-existent planet" {
        // When
        val result = service.deletePlanet(UUID.randomUUID())

        // Then
        result shouldBe false
    }

    "getAllPlanets should return all planets" {
        // Given
        service.createPlanet("Earth")
        service.createPlanet("Mars")

        // When
        val planets = service.getAllPlanets()

        // Then
        planets.size shouldBe 2
        planets.map { it.name } shouldContainAll listOf("Earth", "Mars")
    }

    "getAllPlanets should return empty list when no planets exist" {
        // When
        val planets = service.getAllPlanets()

        // Then
        planets shouldBe emptyList()
    }
})