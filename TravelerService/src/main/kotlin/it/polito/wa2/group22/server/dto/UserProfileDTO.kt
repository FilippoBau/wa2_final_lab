package it.polito.wa2.group22.server.dto

data class UserProfileDTO (
    var name: String,
    var address: String? = null,
    var date_of_birth: String? = null,
    var telephone_number: String? = null
    )