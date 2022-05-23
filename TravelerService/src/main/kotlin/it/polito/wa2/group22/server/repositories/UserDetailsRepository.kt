package it.polito.wa2.group22.server.repositories

import it.polito.wa2.group22.server.entities.UserDetails
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.net.Inet4Address
import javax.transaction.Transactional


@Repository
interface UserDetailsRepository : CrudRepository<UserDetails, String>{
    fun findByUsername(username: String): UserDetails?

    @Transactional
    @Modifying
    @Query("UPDATE UserDetails u set u.address = ?1, u.date_of_birth = ?2, u.name = ?3, u.telephone_number = ?4  where u.username = ?5")
    fun updateUserProfileByUsername(address: String?, date_of_birth: String?, name: String?, telephone_number: String?, username: String): Int
}