package dev.macpherson.example.micronaut4kotlin2.api.dto

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import io.micronaut.context.annotation.Value
import io.micronaut.http.HttpStatus
import io.micronaut.problem.HttpStatusType
import io.micronaut.problem.violations.Violation
import io.micronaut.serde.annotation.Serdeable
import org.zalando.problem.AbstractThrowableProblem
import org.zalando.problem.Exceptional
import org.zalando.problem.StatusType
import org.zalando.problem.ThrowableProblem
import java.net.URI
import java.util.Collections

fun createTypeUri(
    type: String,
    @Value("\${problem.typeBaseUri:`https://localhost/errors/`}") defaultTypeBaseUri: String = ""
) : URI {
    return URI(defaultTypeBaseUri + type)
}

@Serdeable
class NotFoundProblem(
    entityType: String,
    identifier: String,
    type: String = "not-found",
    title: String = "Not Found",
    detail: String? = null
) : AbstractThrowableProblem(
    createTypeUri(type) ,
    title,
    HttpStatusType(HttpStatus.NOT_FOUND),
    detail ?: "$entityType not found with identifier: $identifier",
    null,
    null,
    mapOf("identifier" to identifier, "entityType" to entityType)

) {
    @JsonIgnore
    override fun getCause(): Exceptional? = cause
}

@Serdeable
class BadRequestProblem(
    type: String = "bad-request",
    title: String = "Bad Request",
    detail: String? = null
) : AbstractThrowableProblem(
    createTypeUri(type) ,
    title,
    HttpStatusType(HttpStatus.BAD_REQUEST),
    detail ?: "Your request was not understood by the system, please check it and try again",
) {
    @JsonIgnore
    override fun getCause(): Exceptional? = cause
}

@Serdeable
class ForbiddenProblem(
    type: String = "forbidden",
    title: String = "Forbidden",
    detail: String? = null
) : AbstractThrowableProblem(
    createTypeUri(type) ,
    title,
    HttpStatusType(HttpStatus.FORBIDDEN),
    detail ?: "You do not have permission to perform this action",
) {
    @JsonIgnore
    override fun getCause(): Exceptional? = cause
}

// This constraint violation function sucks in its current form
// The default implementation has hardcoded links to zalando's error page
// Which I wanted to get rid of and succeeded but this needs refactoring to be more concise
//
// If I omit the overridden getters the properties are not serialised, however having them
// return a class member property of the same name, i.e. 'type' introduces a recursive
// synthetic property accessor error.
//
// I don't want to spend any more time on this right now, so just made them private class members
@JsonIgnoreProperties(value = ["stackTrace", "localizedMessage", "message"])
@Serdeable
class CustomConstraintViolationThrowableProblem(
    private val problemType: String = "constrain-violation",
    private val problemTitle: String = "Bad Request",
    private val problemDetail: String? = null,
    val violations: List<Violation?>
) : AbstractThrowableProblem(

) {


    override fun getType(): URI {
        return createTypeUri(problemType)
    }

    override fun getTitle(): String {
        return problemTitle
    }

    override fun getStatus(): StatusType {
        return HttpStatusType(HttpStatus.BAD_REQUEST)
    }

    override fun getDetail(): String? {
        return problemDetail
    }

    override fun getCause(): Exceptional {
        TODO("Not yet implemented")
    }
}