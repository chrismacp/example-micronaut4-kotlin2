package dev.macpherson.example.micronaut4kotlin2.api.controller

import dev.macpherson.example.micronaut4kotlin2.api.dto.BadRequestProblem
import dev.macpherson.example.micronaut4kotlin2.api.dto.NotFoundProblem
import dev.macpherson.example.micronaut4kotlin2.api.dto.PlanetRequest
import dev.macpherson.example.micronaut4kotlin2.api.dto.PlanetResponse
import dev.macpherson.example.micronaut4kotlin2.api.dto.SuccessResponse
import dev.macpherson.example.micronaut4kotlin2.api.dto.UpdatePlanetRequest
import dev.macpherson.example.micronaut4kotlin2.domain.service.PlanetService
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.uri.UriBuilder
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.annotation.Put
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.annotation.Status
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import io.micronaut.validation.Validated
import jakarta.validation.Valid
import java.util.UUID

@Controller("/api/planets")
@Validated
@ExecuteOn(TaskExecutors.IO)
class PlanetController(
    private val planetService: PlanetService
) {

    @Post
    @Status(HttpStatus.CREATED)
    fun createPlanet(@Valid @Body request: PlanetRequest): HttpResponse<SuccessResponse<PlanetResponse>> {
        val planet = planetService.createPlanet(request.name, request.description)
        val location = buildPlanetUri(planet.id)
        return HttpResponse
            .created(SuccessResponse(PlanetResponse.from(planet)))
            .header("Location", location.toString())
            .contentType(MediaType.APPLICATION_JSON)
    }

    @Get
    fun getAllPlanets(): HttpResponse<SuccessResponse<List<PlanetResponse>>> {
        val planets = planetService.getAllPlanets()
            .map { PlanetResponse.from(it) }
        return HttpResponse.ok(SuccessResponse(planets))
    }

    @Get("/{id}")
    fun getPlanetById(@PathVariable id: UUID): HttpResponse<SuccessResponse<PlanetResponse>> {
        return planetService.getPlanet(id)
            ?.let { HttpResponse.ok(SuccessResponse(PlanetResponse.from(it))) }
            ?: throw NotFoundProblem(entityType = "Planet", identifier = id.toString())
    }

    @Get("/search")
    fun getPlanetByName(@QueryValue name: String): HttpResponse<SuccessResponse<PlanetResponse>> {
        return planetService.getPlanetByName(name)
            ?.let { HttpResponse.ok(SuccessResponse(PlanetResponse.from(it))) }
            ?: throw NotFoundProblem(
                entityType = "Planet",
                identifier = name,
                detail = "Planet not found with name: $name"
            )
    }

    @Put("/{id}")
    fun updatePlanet(
        @PathVariable id: UUID,
        @Valid @Body request: UpdatePlanetRequest
    ): HttpResponse<SuccessResponse<PlanetResponse>> {
        return try {
            val updated = planetService.updatePlanet(id, request.name, request.description)
            HttpResponse.ok(SuccessResponse(PlanetResponse.from(updated)))
        } catch (e: NoSuchElementException) {
            throw NotFoundProblem(entityType = "Planet", identifier = id.toString())
        } catch (e: IllegalArgumentException) {
            throw BadRequestProblem(detail = e.message)
        }
    }

    @Delete("/{id}")
    @Status(HttpStatus.NO_CONTENT)
    fun deletePlanet(@PathVariable id: UUID) {
        if (!planetService.deletePlanet(id)) {
            throw NotFoundProblem(entityType = "Planet", identifier = id.toString())
        }
    }

    private fun buildPlanetUri(id: UUID): String {
        return UriBuilder.of("/api/planets/{id}")
            .scheme("http")
            .expand(mutableMapOf("id" to id.toString()))
            .toString()
    }
}