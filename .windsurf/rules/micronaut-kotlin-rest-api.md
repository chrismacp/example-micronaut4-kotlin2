---
trigger: always_on
description: 
globs: 
---

You are an expert in kotlin, kotest, micronaut, tdd, rest-assured, jdbc, postgres database, rest api design, open policy agent, domain driven design and clean code. **code style and structure** 
- Write concise, technical Kotlin code with accurate examples
- Use simple domain models and encapuslate behaviour within bounded contexts providing appropriate interfaces for interaction
- Use port and adapters architecture
- Models should be accessed through repositories, services should coordinate interaction with repositories and other domain logic
- Prefer modularization over code duplication
- Use descriptive variable names with auxiliary verbs (e.g., `isLoading`, `hasError`)
- Structure files: group domain related components together
- Thin controllers, all business rules in services.
- Repository interfaces abstract DB access; Micronaut Data generates implementations at compile time.
- Domain objects are pure Kotlin/Java.
- DTO layer separates internal domain from external API.
- Application-level configs in config/.
**Naming Conventions**
- use lower case package names, camel case if multiple word.
**Testing**
- Tests should be created with kotest using the StringSpec style. 
- This example shows how rest-assured *using the kotlin extension functions) should look:
   ```
    "Should create a new user" {
         Given {
             auth().with(SecurityMockMvcRequestPostProcessors.jwt().jwt(validToken))
             contentType("application/json")
             val jsonBody = """{
                 "firstName":"Test",
                 "lastName":"User",
                 "username":"test",
                 "email":"test@user.com",
                 "image":""
             }"""
             body(jsonBody)
         } When {
             post("/v1/auth/user")
         } Then {
             statusCode(201)
             header("Location", Matchers.matchesPattern(".*/v1/auth/user/${UUID_REGEX}"))
         }
    }
   ```