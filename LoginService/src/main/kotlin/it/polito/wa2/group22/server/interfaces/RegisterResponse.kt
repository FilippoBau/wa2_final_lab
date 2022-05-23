package it.polito.wa2.group22.server.interfaces

import it.polito.wa2.group22.server.utils.RegistrationResult
import java.util.*

interface RegisterResponse
data class RegisterResponseValid(
    val provisional_id: UUID,
    val email: String
) : RegisterResponse

data class RegisterResponseError(
    val errorType: RegistrationResult
) : RegisterResponse