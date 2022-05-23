    package it.polito.wa2.group22.server.dto

import it.polito.wa2.group22.server.entities.User

data class UserDTO(
    var userId: Long?,
    var nickname: String,
    var password: String,
    var email: String
)

//Convert user entity to userDTO
fun User.toDTO(): UserDTO {
    return UserDTO(this.id, this.username, this.password, this.email)
}
