package it.polito.wa2.group22.server.repositories

import it.polito.wa2.group22.server.entities.User
import org.springframework.data.repository.CrudRepository

interface UserRepository: CrudRepository<User, Long> {

    fun findByUsername(username: String): User?;
    fun findByEmail(email: String): User?;
}