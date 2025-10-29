package dev.macpherson.example.micronaut4kotlin2.infra.repository

import dev.macpherson.example.micronaut4kotlin2.domain.model.Planet
import dev.macpherson.example.micronaut4kotlin2.domain.repository.PlanetRepository
import jakarta.inject.Singleton
import java.sql.ResultSet
import java.util.UUID
import javax.sql.DataSource

@Singleton
class JdbcPlanetRepository(
    private val dataSource: DataSource
) : PlanetRepository {

    override fun save(planet: Planet): Planet {
        val sql = """
            INSERT INTO planets (id, name, description)
            VALUES (?, ?, ?)
        """.trimIndent()

        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setObject(1, planet.id)
                stmt.setString(2, planet.name)
                stmt.setString(3, planet.description)
                stmt.executeUpdate()
            }
        }
        return planet
    }

    override fun findById(id: UUID): Planet? {
        val sql = "SELECT id, name, description FROM planets WHERE id = ?"

        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setObject(1, id)
                stmt.executeQuery().use { rs ->
                    return if (rs.next()) {
                        mapResultSetToPlanet(rs)
                    } else {
                        null
                    }
                }
            }
        }
    }

    override fun findByName(name: String): Planet? {
        val sql = "SELECT id, name, description FROM planets WHERE LOWER(name) = LOWER(?)"

        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, name)
                stmt.executeQuery().use { rs ->
                    return if (rs.next()) {
                        mapResultSetToPlanet(rs)
                    } else {
                        null
                    }
                }
            }
        }
    }

    override fun findAll(): List<Planet> {
        val sql = "SELECT id, name, description FROM planets ORDER BY name"
        val planets = mutableListOf<Planet>()

        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.executeQuery().use { rs ->
                    while (rs.next()) {
                        planets.add(mapResultSetToPlanet(rs))
                    }
                }
            }
        }
        return planets
    }

    override fun update(planet: Planet): Planet {
        val sql = """
            UPDATE planets
            SET name = ?, description = ?
            WHERE id = ?
        """.trimIndent()

        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setString(1, planet.name)
                stmt.setString(2, planet.description)
                stmt.setObject(3, planet.id)
                val rowsUpdated = stmt.executeUpdate()
                if (rowsUpdated == 0) {
                    throw IllegalArgumentException("Planet with id ${planet.id} not found")
                }
            }
        }
        return planet
    }

    override fun delete(id: UUID): Boolean {
        val sql = "DELETE FROM planets WHERE id = ?"

        dataSource.connection.use { conn ->
            conn.prepareStatement(sql).use { stmt ->
                stmt.setObject(1, id)
                val rowsDeleted = stmt.executeUpdate()
                return rowsDeleted > 0
            }
        }
    }

    private fun mapResultSetToPlanet(rs: ResultSet): Planet {
        return Planet(
            id = rs.getObject("id", UUID::class.java),
            name = rs.getString("name"),
            description = rs.getString("description")
        )
    }
}
