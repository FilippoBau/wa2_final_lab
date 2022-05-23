package it.polito.wa2.group22.server.services

import it.polito.wa2.group22.server.dto.TicketPurchasedDTO
import it.polito.wa2.group22.server.dto.UserProfileDTO
import it.polito.wa2.group22.server.entities.UserDetails
import it.polito.wa2.group22.server.repositories.TicketPurchasedRepository
import it.polito.wa2.group22.server.repositories.UserDetailsRepository
import org.springframework.stereotype.Service

@Service
class AdminService(
        val userDetailsRepository: UserDetailsRepository,
        val ticketPurchasedRepository: TicketPurchasedRepository
) {

    fun getTravelerProfile(userID: String): UserProfileDTO {
        val profile = userDetailsRepository.findById(userID).orElse(null)
        if (profile != null) {
            return UserProfileDTO(
                    profile.name!!,
                    profile.address,
                    profile.date_of_birth,
                    profile.telephone_number
            )
        } else {
            throw IllegalArgumentException("The User doesn't exist")
        }
    }

    fun getTravelerTickets(userID: String): List<TicketPurchasedDTO> {
        val user = userDetailsRepository.findById(userID).orElse(null)
        if (user == null) {
            throw IllegalArgumentException("The User doesn't exist")
        } else {
            return ticketPurchasedRepository.getAllTicketsByUserDetails(UserDetails(user.username)).map {
                TicketPurchasedDTO(it.sub, it.iat, it.exp, it.zid, it.jws)
            }
        }
    }

    fun getTravelers(): List<UserProfileDTO> {
        return userDetailsRepository.findAll().map {
            UserProfileDTO(it.name!!, it.address, it.date_of_birth, it.telephone_number)
        }
    }
}
