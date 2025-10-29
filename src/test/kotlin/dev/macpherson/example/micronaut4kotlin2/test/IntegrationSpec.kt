package dev.macpherson.example.micronaut4kotlin2.test

import io.kotest.core.spec.style.StringSpec
import io.micronaut.runtime.server.EmbeddedServer
import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import jakarta.inject.Inject
import javax.sql.DataSource


abstract class IntegrationSpec(
    body: StringSpec.() -> Unit
) : StringSpec(body) {

    @Inject
    lateinit var embeddedServer: EmbeddedServer

    @Inject
    lateinit var dataSource: DataSource

    init {
        beforeTest {
            RestAssured.baseURI = "http://localhost"
            RestAssured.port = embeddedServer.port
            RestAssured.requestSpecification = RequestSpecBuilder()
                .setContentType("application/json")
                .build()
        }

        afterTest {
            // Clean up database after each test
            dataSource.connection.use { conn ->
                conn.createStatement().use { stmt ->
                    stmt.execute("TRUNCATE TABLE planets CASCADE")
                }
            }
        }
    }
}