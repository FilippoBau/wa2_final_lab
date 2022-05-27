package it.polito.wa2.group22.ticketcatalogservice.dtos

import org.springframework.security.core.authority.SimpleGrantedAuthority

data class UserDetailsDTO(
    val userId: Long,
    var roles: MutableList<SimpleGrantedAuthority>?
)