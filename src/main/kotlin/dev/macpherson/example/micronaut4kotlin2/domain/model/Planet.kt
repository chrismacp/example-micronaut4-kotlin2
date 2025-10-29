package dev.macpherson.example.micronaut4kotlin2.domain.model

import java.util.UUID

/**
 * Represents a Planet in the system.
 *
 * @property id The unique identifier of the planet (null for new planets)
 * @property name The name of the planet (required, max 100 characters)
 * @property description An optional description of the planet
 */
data class Planet(
    val id: UUID,
    val name: String,
    val description: String? = null
) {
    init {
        require(name.isNotBlank()) { "Planet name cannot be blank" }
        require(name.length <= 100) { "Planet name cannot be longer than 100 characters" }
    }

    companion object {
        /**
         * Creates a new Planet with the given name and description.
         * @throws IllegalArgumentException if the name is blank or longer than 100 characters
         */
        fun create(name: String, description: String? = null): Planet {
            return Planet(UUID.randomUUID(), name, description)
        }
    }
}