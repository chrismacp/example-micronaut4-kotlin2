package dev.macpherson.example.micronaut4kotlin2.infra.repository

import dev.macpherson.example.micronaut4kotlin2.domain.model.Planet
import dev.macpherson.example.micronaut4kotlin2.domain.repository.PlanetRepository
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.collections.find
import kotlin.collections.toList

class InMemoryPlanetRepository : PlanetRepository {
    private val planets = ConcurrentHashMap<UUID, Planet>()

    override fun save(planet: Planet): Planet {
        planets[planet.id] = planet
        return planet
    }

    override fun findById(id: UUID): Planet? = planets[id]

    override fun findByName(name: String): Planet? =
        planets.values.find { it.name.equals(name, ignoreCase = true) }

    override fun findAll(): List<Planet> = planets.values.toList()

    override fun update(planet: Planet): Planet {
        require(planets.containsKey(planet.id)) { "Planet with id ${planet.id} not found" }
        planets[planet.id] = planet
        return planet
    }

    override fun delete(id: UUID): Boolean = planets.remove(id) != null

    fun clear() = planets.clear()
}