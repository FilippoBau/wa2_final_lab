package it.polito.wa2.group22.ticketcatalogservice.dtos

data class UserProfileDTO(
    var userId: Long?,
    val name: String?,
    val role: String?,
    val address: String?,
    val dateOfBirth: String?,
    val telephoneNumber: String?,
)