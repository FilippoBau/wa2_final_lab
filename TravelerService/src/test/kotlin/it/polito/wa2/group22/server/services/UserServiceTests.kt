package it.polito.wa2.group22.server.services

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import it.polito.wa2.group22.server.controllers.Action
import it.polito.wa2.group22.server.dto.TicketPurchasedDTO
import it.polito.wa2.group22.server.dto.UserProfileDTO
import it.polito.wa2.group22.server.dto.toDTO
import it.polito.wa2.group22.server.entities.TicketPurchased
import it.polito.wa2.group22.server.entities.UserDetails
import it.polito.wa2.group22.server.interfaces.UserServiceResponseError
import it.polito.wa2.group22.server.interfaces.UserServiceResponseValid
import it.polito.wa2.group22.server.repositories.TicketPurchasedRepository
import it.polito.wa2.group22.server.repositories.UserDetailsRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.sql.Timestamp
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserServiceTests {

    companion object {
        @Container
        val postgres = PostgreSQLContainer<Nothing>("postgres:latest")

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }
        }
    }

    @Autowired
    lateinit var userDetailsRepository: UserDetailsRepository

    @Autowired
    lateinit var ticketPurchasedRepository: TicketPurchasedRepository

    @Autowired
    lateinit var userService: UserService

    @Value("\${jwt.keyTicket}")
    lateinit var keyTicket: String

    @Test
    fun getUserProfileValid(){
        val user = UserDetails(
            "getUserProfileValid",
            "TestUser1",
            "TestAddress1",
            "28/21/1995",
            "3364459928"
        )

        val userSaved = userDetailsRepository.save(user)

        val userReturned = userService.getUserProfile(userSaved.username)
        val userServiceResponseValid = UserServiceResponseValid(userDetails = UserProfileDTO(user.name!!, user.address!!, user.date_of_birth!!, user.telephone_number!!))
        Assertions.assertEquals(userServiceResponseValid, userReturned)
    }

    @Test
    fun getUserProfileNotValidUsername(){
        val userReturned = userService.getUserProfile("userName")
        val userServiceResponseError = UserServiceResponseError("UserProfile not found")
        Assertions.assertEquals(userServiceResponseError, userReturned)
    }

    @Test
    fun updateUserProfileValid(){
        val user = UserDetails(
            "updateUserProfileValid",
            "TestUser1",
        )
        val userSaved = userDetailsRepository.save(user)
        val userUpdated = UserProfileDTO(
            "name",
            "address",
            "21/07/1997",
            "3798415981"
        )
        val res = userService.updateUserProfile(userUpdated, userSaved.username)
        val userServiceResponseValidUpdate = UserServiceResponseValid(result = true)
        Assertions.assertEquals(userServiceResponseValidUpdate, res)
        val userReturned = userService.getUserProfile(userSaved.username)
        val userServiceResponseValidGet = UserServiceResponseValid(userDetails = userUpdated)
        Assertions.assertEquals(userServiceResponseValidGet, userReturned)
    }

    // TODO: to be fixed
    @Test
    fun updateUserProfileNotValidDate(){
        val user = UserDetails(
            "updateUserProfileNotValidDate",
            "TestUser1",
            "address",
            "21/07/1997",
            "3798415981"
        )
        val userSaved = userDetailsRepository.save(user)
        val userUpdated = UserProfileDTO(
            "name",
            "address",
            "2",
            "3798415981"
        )
        val res = userService.updateUserProfile(userUpdated, userSaved.username)
        val userServiceResponseErrorUpdate = UserServiceResponseError("Field validation failed!")
        Assertions.assertEquals(userServiceResponseErrorUpdate, res)
    }

    @Test
    fun updateUserProfileNotValidTel(){
        val user = UserDetails(
            "updateUserProfileNotValidTel",
            "TestUser1",
            "address",
            "21/07/1997",
            "3798415981"
        )
        val userSaved = userDetailsRepository.save(user)
        val userUpdated = UserProfileDTO(
            "name",
            "address",
            "28/05/1777",
            "3"
        )
        val res = userService.updateUserProfile(userUpdated, userSaved.username)
        val userServiceResponseError = UserServiceResponseError("Field validation failed!")
        Assertions.assertEquals(userServiceResponseError, res)
    }

    @Test
    fun getUserTicketsValid(){

        val user = UserDetails(
            "getUserTicketsValid",
            "TestUser1",
            "TestAddress1",
            "28/21/1995",
            "3364459928"
        )
        //Save
        val userSaved = userDetailsRepository.save(user)
        Assertions.assertNotNull(userSaved)
        val zones = "1,2,3"

        // Generating ticket
        /*val token = generateTicketToken(user.username, Date.from(Instant.now().plus(1, ChronoUnit.HOURS)), zones)
        val ticket = TicketPurchased(
            iat = Timestamp(System.currentTimeMillis()),
            exp = Timestamp(System.currentTimeMillis() + 3600000),
            zid = zones,
            jws = token
        )*/

        val action = Action("buy_tickets", 1, zones)

        // Save
        val ticketPurchased = userService.buyTickets(userSaved.username, action)
        val responseValid = ticketPurchased as UserServiceResponseValid
        //val ticketPurchased = ticketPurchasedRepository.save(ticket)
        //Assertions.assertNotNull(ticketPurchased)

        //user.addTicket(ticketPurchased)

        //println(ticketPurchased.userDetails!!.toDTO())


        //Assertions.assertNotEquals(userSaved.tickets.size, 0)

        val res = userService.getUserTickets(userSaved.username)
        /*val ticketPurchasedDTO = mutableListOf<TicketPurchasedDTO>()
        ticketPurchasedDTO.add(responseValid.tickets[0])
        val userServiceResponseValid = UserServiceResponseValid(tickets = ticketPurchasedDTO)*/
        Assertions.assertEquals(ticketPurchased, res)
    }

    @Test
    fun getUserTicketsNotFound(){
        val user = UserDetails(
            "getUserTicketsNotFound",
            "TestUser1",
            "TestAddress1",
            "28/21/1995",
            "3364459928"
        )
        val userSaved = userDetailsRepository.save(user)
        val tickets = userService.getUserTickets(userSaved.username)
        val userServiceResponseError = UserServiceResponseError("No tickets found")
        Assertions.assertEquals(userServiceResponseError, tickets)
    }

    @Test
    fun buyTicketsValid(){
        val user = UserDetails(
            "buyTicketsValid",
            "TestUser1",
            "TestAddress1",
            "28/21/1995",
            "3364459928"
        )
        val action = Action("buy_tickets", 2, "1,2,3")
        val userSaved = userDetailsRepository.save(user)
        /*val token = generateTicketToken(userSaved.username, Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
        val ticket = TicketPurchased(
            iat = Timestamp(System.currentTimeMillis()),
            exp = Timestamp(System.currentTimeMillis() + 3600000),
            zid = action.zones,
            jws = token
        )*/
        val tickets = userService.buyTickets(userSaved.username, action)
        /*val ticketsResult = mutableListOf<TicketPurchasedDTO>()
        ticketsResult.add(ticket.toDTO())
        ticketsResult.add(ticket.toDTO())
        val userServiceResponseValid = UserServiceResponseValid(tickets = ticketsResult)*/
        val ticketsParsed = (tickets as UserServiceResponseValid).tickets
        Assertions.assertEquals(ticketsParsed!!.size, 2)
    }

    @Test
    fun buyTicketsNotValidUsername(){
        val action = Action("buy_tickets", 2, "1,2,3")
        val tickets = userService.buyTickets("", action)
        val userServiceResponseError = UserServiceResponseError("Username value is invalid")
        Assertions.assertEquals(userServiceResponseError,tickets)
    }

    @Test
    fun buyTicketsNotValidQuantity(){
        val action = Action("buy_tickets", 0, "1,2,3")
        val tickets = userService.buyTickets("ValidUsername", action)
        val userServiceResponseError = UserServiceResponseError("Quantity value is invalid")
        Assertions.assertEquals(userServiceResponseError,tickets)
    }

    @Test
    fun buyTicketsNotValidCommand(){
        val action = Action("buy_ticketsERROR", 2, "1,2,3")
        val tickets = userService.buyTickets("ValidUsername", action)
        val userServiceResponseError = UserServiceResponseError("Command value is invalid")
        Assertions.assertEquals(userServiceResponseError,tickets)
    }

    @Test
    fun buyTicketsNotValidZones(){
        val action = Action("buy_tickets", 2, "")
        val tickets = userService.buyTickets("ValidUsername", action)
        val userServiceResponseError = UserServiceResponseError("Zones value is invalid")
        Assertions.assertEquals(userServiceResponseError,tickets)
    }

    @Test
    fun buyTicketsUserNotExists(){
        val action = Action("buy_tickets", 2, "1,2,3")
        val tickets = userService.buyTickets("InvalidUser", action)
        val userServiceResponseError = UserServiceResponseError("User not found")
        Assertions.assertEquals(userServiceResponseError,tickets)
    }

    fun generateTicketToken(username: String, exp: Date, zones: String): String{
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(Date.from(Instant.now()))
            .setExpiration(exp)
            .claim("zid", zones)
            .signWith(Keys.hmacShaKeyFor(keyTicket.toByteArray())).compact()
    }
}