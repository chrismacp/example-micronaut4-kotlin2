package dev.macpherson.example.micronaut4kotlin2.domain.model

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.kotest.matchers.types.shouldBeInstanceOf

class PlanetTest : StringSpec({
    "creating a planet with valid parameters should succeed" {
        // Given
        val name = "Earth"
        val description = "Blue planet"

        // When
        val planet = Planet.create(name, description)

        // Then
        planet.name shouldBe name
        planet.description shouldBe description
        planet.id shouldNotBe null
    }

    "creating a planet with blank name should fail" {
        // Given
        val blankName = " "
        val description = "Invalid planet"

        // When
        val exception = runCatching {
            Planet.create(blankName, description)
        }.exceptionOrNull()

        // Then
        exception.shouldBeInstanceOf<IllegalArgumentException>()
        exception shouldHaveMessage "Planet name cannot be blank"
    }

    "creating a planet with name longer than 100 characters should fail" {
        // Given
        val longName = "X".repeat(101)

        // When
        val exception = runCatching {
            Planet.create(longName, "Too long name")
        }.exceptionOrNull()

        // Then
        exception.shouldBeInstanceOf<IllegalArgumentException>()
        exception shouldHaveMessage "Planet name cannot be longer than 100 characters"
    }

    "creating a planet with exactly 100 character name should succeed" {
        // Given
        val maxLengthName = "X".repeat(100)

        // When
        val planet = Planet.create(maxLengthName)

        // Then
        planet.name shouldBe maxLengthName
        planet.name.length shouldBe 100
    }

    "copying a planet should maintain validation" {
        // Given
        val original = Planet.create("Mars")

        // When
        val exception = runCatching {
            original.copy(name = " ")
        }.exceptionOrNull()

        // Then
        exception.shouldBeInstanceOf<IllegalArgumentException>()
        exception shouldHaveMessage "Planet name cannot be blank"
    }

    "creating a planet with null description should be allowed" {
        // Given
        val name = "Venus"

        // When
        val planet = Planet.create(name, null)

        // Then
        planet.name shouldBe name
        planet.description shouldBe null
    }

    "creating a planet with empty description should be allowed" {
        // Given
        val name = "Mercury"
        val emptyDescription = ""

        // When
        val planet = Planet.create(name, emptyDescription)

        // Then
        planet.name shouldBe name
        planet.description shouldBe emptyDescription
    }
})