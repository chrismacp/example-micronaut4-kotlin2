package dev.macpherson.example.micronaut4kotlin2.api.controller

import dev.macpherson.example.micronaut4kotlin2.api.dto.PlanetRequest
import dev.macpherson.example.micronaut4kotlin2.domain.model.Planet
import dev.macpherson.example.micronaut4kotlin2.domain.service.PlanetService
import dev.macpherson.example.micronaut4kotlin2.test.IntegrationSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.micronaut.http.HttpStatus
import io.micronaut.test.extensions.kotest5.annotation.MicronautTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID

@MicronautTest
class PlanetControllerTest : IntegrationSpec({

    "create planet should return 201 and location header when valid request" {

        // Given
        val planetId = UUID.randomUUID()
        val planetName = "Earth"
        val planetDescription = "Blue planet"
        val planet = Planet(planetId, planetName, planetDescription)

        val mockPlanetService = mockk<PlanetService>()
        every { mockPlanetService.createPlanet(any(), any()) } returns planet

        val request = PlanetRequest(planetName, planetDescription)
        val planetController = PlanetController(mockPlanetService)

        // When
        val response = planetController.createPlanet(request)

        // Then
        response.status shouldBe HttpStatus.CREATED
        response.header("Location") shouldNotBe null

        verify { mockPlanetService.createPlanet(planetName, planetDescription) }
    }
})