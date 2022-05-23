package it.polito.wa2.group22.server.repositories

import it.polito.wa2.group22.server.dto.TicketPurchasedDTO
import it.polito.wa2.group22.server.entities.TicketPurchased
import it.polito.wa2.group22.server.entities.UserDetails
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TicketPurchasedRepository : CrudRepository<TicketPurchased, String>{

    //@Query("SELECT t FROM TicketPurchased AS t WHERE t.userDetails = ?1")
    fun getAllTicketsByUserDetails(userDetails: UserDetails) : List<TicketPurchased>

}