package dev.macpherson.example.micronaut4kotlin2.api.exception

import dev.macpherson.example.micronaut4kotlin2.api.dto.CustomConstraintViolationThrowableProblem
import io.micronaut.context.annotation.Replaces
import io.micronaut.context.annotation.Requires
import io.micronaut.core.annotation.Order
import io.micronaut.core.order.Ordered
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Produces
import io.micronaut.http.server.exceptions.ExceptionHandler
import io.micronaut.http.server.exceptions.response.ErrorContext
import io.micronaut.http.server.exceptions.response.ErrorResponseProcessor
import io.micronaut.problem.violations.Violation
import io.micronaut.validation.exceptions.ConstraintExceptionHandler
import jakarta.inject.Singleton
import jakarta.validation.ConstraintViolation
import jakarta.validation.ConstraintViolationException

@Produces
@Singleton
@Requires(classes = [ConstraintViolationException::class, ExceptionHandler::class])
@Replaces(ConstraintExceptionHandler::class)
@Order(Ordered.HIGHEST_PRECEDENCE)
class ConstraintViolationExceptionHandler(
     private val responseProcessor: ErrorResponseProcessor<*>
) : ExceptionHandler<ConstraintViolationException, HttpResponse<*>> {

    override fun handle(request: HttpRequest<*>, exception: ConstraintViolationException): HttpResponse<*> {
        val httpStatus = HttpStatus.BAD_REQUEST
        val violations = exception.constraintViolations.map { createViolation(it)}

        return responseProcessor.processResponse(
            ErrorContext.builder(request)
                .cause(
                    CustomConstraintViolationThrowableProblem(
                        "constraint-violation",
                        "Bad Request",
                        "Check to ensure the provided data is correct",
                        violations
                    )
                )
                .errorMessage(exception.message)
                .build(),
            HttpResponse.status<Any?>(httpStatus)
        )
    }

    /**
     *
     * @param constraintViolation Constraint Violation
     * @return A Violation
     */
    protected fun createViolation(constraintViolation: ConstraintViolation<*>?): Violation {
        return Violation(constraintViolation?.propertyPath.toString(), constraintViolation?.message)
    }
}
