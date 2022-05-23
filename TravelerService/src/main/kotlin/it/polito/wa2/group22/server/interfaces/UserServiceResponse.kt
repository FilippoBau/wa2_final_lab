package it.polito.wa2.group22.server.interfaces

import it.polito.wa2.group22.server.dto.TicketPurchasedDTO
import it.polito.wa2.group22.server.dto.UserProfileDTO
import it.polito.wa2.group22.server.entities.UserDetails

interface UserServiceResponse {
}

data class UserServiceResponseValid(
    val userDetails: UserProfileDTO? = null,
    val tickets: List<TicketPurchasedDTO>? = null,
    val result: Boolean? = null
) : UserServiceResponse

data class UserServiceResponseError(
    val errorType: String
) : UserServiceResponse