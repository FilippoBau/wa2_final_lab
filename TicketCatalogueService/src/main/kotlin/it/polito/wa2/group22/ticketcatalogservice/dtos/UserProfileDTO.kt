package it.polito.wa2.group22.ticketcatalogservice.dtos

data class UserProfileDTO(
    var username: String?,
    val name: String?,
    val role: String?,
    val address: String?,
    val date_of_birth: String?,
    val telephone_number: String?,
)