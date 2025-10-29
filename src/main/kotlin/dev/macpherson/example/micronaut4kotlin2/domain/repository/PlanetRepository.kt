package dev.macpherson.example.micronaut4kotlin2.domain.repository

import dev.macpherson.example.micronaut4kotlin2.domain.model.Planet
import java.util.UUID

interface PlanetRepository {
    fun save(planet: Planet): Planet
    fun findById(id: UUID): Planet?
    fun findByName(name: String): Planet?
    fun findAll(): List<Planet>
    fun update(planet: Planet): Planet
    fun delete(id: UUID): Boolean
}