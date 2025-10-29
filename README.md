## Planet Service 

This service is a simple example of a planet service that uses Micronaut, Kotlin, Kotest, and Rest Assured.

I will be using AI to build this service based upon the following details

## Application Build Tools

- It will use Micronaut for the application framework
- It will use a postgres database to store the planet names
- It will use JDBC (not JPA) for persistence, however domain entity classes should be plain kotlin data classes
- It will use Flyway to manage the database schema
- It will use Kotest for testing via the StringSpec Style
- It will use Rest Assured for integration testing and make use of the kotlin extensions for Rest Assured (Given, When, Then)
- It will use Testcontainers to manage the database
- It will use Gradle for the build tool

## Application Features

- This is a rest API for a planet service that stores planet names in a database
- Each planet entity has an ID and name   
- The rest API operates via JSON input and output formatted according to the JSend specification (https://github.com/omniti-labs/jsend)
- It will have the following endpoints
    - GET /planets
    - GET /planets/{id}
    - PUT /planets/{id}
    - DELETE /planets/{id}

- Planet entity contains an id, name, and description only
- Planet names will be stored in a database
- Planet names will be validated to ensure they are not null or empty
- Planet names will be validated to ensure they are not longer than 100 characters
- Planet names will be validated to ensure they are not longer than 100 characters
- DTO objects will be used to isolate the rest API from the entity objects


## Testing
- The happy paths cover the following to ensure the rest API works as expected
    - GET /planets
    - GET /planets/{id}
    - PUT /planets/{id}
    - DELETE /planets/{id}

- The sad paths cover the following to ensure the rest API fails as expected
    - GET /planets
    - GET /planets/{id}
    - PUT /planets/{id}
    - DELETE /planets/{id}
