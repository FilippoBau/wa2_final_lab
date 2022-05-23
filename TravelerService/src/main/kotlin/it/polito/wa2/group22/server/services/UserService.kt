package it.polito.wa2.group22.server.services

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import it.polito.wa2.group22.server.controllers.Action
import it.polito.wa2.group22.server.dto.TicketPurchasedDTO
import it.polito.wa2.group22.server.dto.UserDetailsDTO
import it.polito.wa2.group22.server.dto.UserProfileDTO
import it.polito.wa2.group22.server.dto.toDTO
import it.polito.wa2.group22.server.entities.TicketPurchased
import it.polito.wa2.group22.server.entities.UserDetails
import it.polito.wa2.group22.server.interfaces.UserServiceResponse
import it.polito.wa2.group22.server.interfaces.UserServiceResponseError
import it.polito.wa2.group22.server.interfaces.UserServiceResponseValid
import it.polito.wa2.group22.server.repositories.TicketPurchasedRepository
import it.polito.wa2.group22.server.repositories.UserDetailsRepository
import it.polito.wa2.group22.server.security.JwtUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Service
class UserService(
    val userDetailsRepository: UserDetailsRepository, val ticketPurchasedRepository: TicketPurchasedRepository
) {

    @Autowired
    lateinit var jwtUtils: JwtUtils

    @Value("\${jwt.key}")
    lateinit var key: String

    @Value("\${jwt.keyTicket}")
    lateinit var keyTicket: String

    private val telephoneRegularExp = Regex("^[0-9]{10}\$")
    private val dateRegularExp = Regex("^([0-2][0-9]|(3)[0-1])(\\/)(((0)[0-9])|((1)[0-2]))(\\/)\\d{4}\$")


    fun getUserProfile(userName: String): UserServiceResponse {
        var userData = userDetailsRepository.findByUsername(userName)
        return if (userData == null) {
            UserServiceResponseError("UserProfile not found")
        } else {
            UserServiceResponseValid(
                UserProfileDTO(
                    userData.name!!, userData.address!!, userData.date_of_birth!!, userData.telephone_number!!
                )
            )
        }
    }

    fun updateUserProfile(userProfileDTO: UserProfileDTO, username: String): UserServiceResponse {
        if (!validateField(userProfileDTO.date_of_birth!!, userProfileDTO.telephone_number!!)) {
            return UserServiceResponseError("Field validation failed!")
        }

        var res = userDetailsRepository.updateUserProfileByUsername(
            userProfileDTO.address,
            userProfileDTO.date_of_birth,
            userProfileDTO.name,
            userProfileDTO.telephone_number,
            username
        )
        if (res == 0) return UserServiceResponseError("UserProfile update failed")
        return UserServiceResponseValid(result = true)
    }

    fun getUserTickets(username: String): UserServiceResponse {
        val userDetails = userDetailsRepository.findByUsername(username)
        val tickets = ticketPurchasedRepository.getAllTicketsByUserDetails(userDetails!!).map{ ticket -> ticket.toDTO() }
        println(tickets.size)
        if (tickets.isNullOrEmpty()) return UserServiceResponseError("No tickets found")
        return UserServiceResponseValid(tickets = tickets)
    }

    fun buyTickets(username: String, action: Action): UserServiceResponse {
        if (username.isNullOrBlank()) return UserServiceResponseError("Username value is invalid")
        if (action.quantity == 0 || action.quantity == null) return UserServiceResponseError("Quantity value is invalid")
        if (action.cmd.isNullOrBlank() || action.cmd != "buy_tickets") return UserServiceResponseError("Command value is invalid")
        if (action.zones.isNullOrBlank()) return UserServiceResponseError("Zones value is invalid")
        val tickets = mutableListOf<TicketPurchasedDTO>()
        val user = userDetailsRepository.findByUsername(username) ?: return UserServiceResponseError("User not found")
        for (i in 1..action.quantity) {
            val token = generateTicketToken(user.username, Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
            val ticket = TicketPurchased(
                iat = Timestamp(System.currentTimeMillis()),
                exp = Timestamp(System.currentTimeMillis() + 3600000),
                zid = action.zones,
                jws = token
            )
            user.addTicket(ticket)
            ticketPurchasedRepository.save(ticket)
            tickets.add(ticket.toDTO())
        }
        return UserServiceResponseValid(tickets = tickets)
    }

    private fun validateField(date_of_birth: String, telephone_number: String): Boolean {
        return dateRegularExp.matches(date_of_birth) && telephoneRegularExp.matches(telephone_number)
    }

    fun generateTicketToken(username: String, exp: Date): String{
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(Date.from(Instant.now()))
            .setExpiration(exp)
            .claim("roles", listOf("USER"))
            .signWith(Keys.hmacShaKeyFor(keyTicket.toByteArray())).compact()
    }

//Function that return all the tickets of a certain user
//If null an error occurred, if empty no user or tickets, else returns a list of tickets
/*fun getUserTickets(jwt : String) : List<TicketPurchasedDTO>?{
    val ret = mutableListOf<TicketPurchasedDTO>()
    //TODO: devo assumere che il jwt sia stato controllato o chiamo la funzione?

    val user : UserDetailsDTO?
    try {
        user = jwtUtils.getDetailsJwt(jwt)
    }catch (e : Exception){
        return null
    }
    if(user == null)
        return null
    val userData : UserDetails
    try {
        userData = userDetailsRepository.findById(user.username).get()
    }
    catch (e : java.util.NoSuchElementException){
        return ret
    }
    userData.tickets.forEach { a ->  ret.add(a.toDTO())}
    return ret
}*/

}