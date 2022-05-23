package it.polito.wa2.group22.server.interfaces

import it.polito.wa2.group22.server.utils.ValidationResult

interface ValidationResponse
data class ValidationResponseValid(
    val userId: Long,
    val username: String,
    val email: String
) : ValidationResponse

data class ValidationResponseError(
    val errorType: ValidationResult
) : ValidationResponse