package it.polito.wa2.group22.server.utils

import org.springframework.security.core.authority.SimpleGrantedAuthority

enum class Role {
    USER, ADMIN
}

fun listStringToListRole(strings: List<String>): MutableList<SimpleGrantedAuthority> {
    val list = mutableListOf<SimpleGrantedAuthority>()
    strings.forEach { a -> if (a == "USER" || a == "ADMIN") list.add(stringToRole[a]!!) }
    return list
}

val stringToRole = mapOf(
    "USER" to SimpleGrantedAuthority("ROLE_"+Role.USER.toString()),
    "ADMIN" to SimpleGrantedAuthority("ROLE_"+Role.ADMIN.toString())
)