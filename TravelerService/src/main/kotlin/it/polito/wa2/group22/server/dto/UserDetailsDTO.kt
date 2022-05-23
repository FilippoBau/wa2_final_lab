    package it.polito.wa2.group22.server.dto

import it.polito.wa2.group22.server.entities.UserDetails
import it.polito.wa2.group22.server.utils.Role
import org.springframework.security.core.authority.SimpleGrantedAuthority

data class UserDetailsDTO(
    var username: String,
    var address: String? = null,
    var date_of_birth: String? = null,
    var telephone_number: String? = null,
    var roles: MutableList<SimpleGrantedAuthority>?
)

fun UserDetails.toDTO(): UserDetailsDTO{
    return UserDetailsDTO(username, address, date_of_birth, telephone_number, null)
}