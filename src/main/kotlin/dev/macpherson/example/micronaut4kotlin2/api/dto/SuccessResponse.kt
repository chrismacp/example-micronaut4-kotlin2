package dev.macpherson.example.micronaut4kotlin2.api.dto

import io.micronaut.serde.annotation.Serdeable

@Serdeable
data class SuccessResponse<T>(
    val data: T
)
