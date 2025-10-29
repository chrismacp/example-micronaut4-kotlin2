package dev.macpherson.example.micronaut4kotlin2.api.controller

import dev.macpherson.example.micronaut4kotlin2.api.dto.PlanetRequest
import dev.macpherson.example.micronaut4kotlin2.api.dto.UpdatePlanetRequest
import dev.macpherson.example.micronaut4kotlin2.test.IntegrationSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.micronaut.http.HttpStatus
import io.micronaut.test.extensions.kotest5.annotation.MicronautTest
import io.restassured.module.kotlin.extensions.Extract
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matchers
import java.util.*

@MicronautTest
class PlanetControllerIntegrationTest : IntegrationSpec({

    "create planet should return 201 and location header when valid request" {
        val request = PlanetRequest("Earth", "Blue planet")

        Given {
            body(request)
        } When {
            post("/api/planets")
        } Then {
            statusCode(HttpStatus.CREATED.code)
            header("Location", notNullValue())
            body("data.id", notNullValue())
            body("data.name", equalTo("Earth"))
            body("data.description", equalTo("Blue planet"))
        }
    }

    "create planet should return 400 when name is empty" {
        val invalidRequest = PlanetRequest("", "Empty name")

        val response = Given {
            body(invalidRequest)
        } When {
            post("/api/planets")
        } Then {
            log().all()
            statusCode(HttpStatus.BAD_REQUEST.code)
            body("status", equalTo(400))
            body("title", equalTo("Bad Request"))
        }
    }

    "get planet by id should return 200 and planet data when exists" {
        // First create a planet
        val createResponse = createTestPlanet("Mars", "Red planet")
        val planetId = (createResponse["data"] as Map<*, *>)["id"] as String

        // Then try to retrieve it
        When {
            get("/api/planets/$planetId")
        } Then {
            statusCode(HttpStatus.OK.code)
            body("data.id", equalTo(planetId))
            body("data.name", equalTo("Mars"))
            body("data.description", equalTo("Red planet"))
        } Extract {
            `as`(Map::class.java)
        }
    }

    "get planet by id should return 404 when not found" {
        val nonExistentId = UUID.randomUUID()

        When {
            get("/api/planets/$nonExistentId")
        } Then {
            statusCode(HttpStatus.NOT_FOUND.code)
            body("status", equalTo(HttpStatus.NOT_FOUND.code))
            body("detail", equalTo("Planet not found with identifier: $nonExistentId"))
        }
    }

    "update planet should return 200 and updated planet when valid request" {
        // First create a planet
        val createResponse = createTestPlanet("Jupiter", "Gas giant")
        val planetId = (createResponse["data"] as Map<*, *>)["id"] as String

        // Then update it
        val updateRequest = UpdatePlanetRequest("Jupiter Updated", "Updated description")

        Given {
            body(updateRequest)
        } When {
            put("/api/planets/$planetId")
        } Then {
            statusCode(HttpStatus.OK.code)
            body("data.id", equalTo(planetId))
            body("data.name", equalTo("Jupiter Updated"))
            body("data.description", equalTo("Updated description"))
        }
    }

    "delete planet should return 204 when successful" {
        // First create a planet
        val createResponse = createTestPlanet("Saturn", "Ringed planet")
        val planetId = (createResponse["data"] as Map<*, *>)["id"] as String

        // Then delete it
        When {
            delete("/api/planets/$planetId")
        } Then {
            statusCode(HttpStatus.NO_CONTENT.code)
        }

        // Verify it's really deleted
        When {
            get("/api/planets/$planetId")
        } Then {
            statusCode(HttpStatus.NOT_FOUND.code)
        }
    }

    "get all planets should return list of planets" {
        // Create some test planets
        createTestPlanet("Mercury", "Closest to the sun")
        createTestPlanet("Venus", "Second planet")

        val response = When {
            get("/api/planets")
        } Then {
            statusCode(HttpStatus.OK.code)
            body("data", Matchers.hasSize<Collection<String>>(2))
        } Extract {
            `as`(Map::class.java)
        }

        with(response["data"] as List<Map<*, *>>) {
            val planets = listOf(this[0]["name"], this[1]["name"])
            planets shouldContainExactlyInAnyOrder listOf("Mercury", "Venus")
        }
    }

    "search planet by name should return planet when found" {
        // Create a test planet
        createTestPlanet("Neptune", "Farthest planet")

        When {
            get("/api/planets/search?name=Neptune")
        } Then {
            statusCode(HttpStatus.OK.code)
            body("data.name", equalTo("Neptune"))
            body("data.description", equalTo("Farthest planet"))
        }
    }
})

// Helper function to create a test planet
private fun createTestPlanet(name: String, description: String): Map<*, *> {
    val request = PlanetRequest(name, description)
    
    return Given {
        body(request)
    } When {
        post("/api/planets")
    } Then {
        statusCode(201)
    } Extract {
        `as`(Map::class.java)
    }
}
