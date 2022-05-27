package it.polito.wa2.group22.ticketcatalogservice.controllers

import it.polito.wa2.group22.ticketcatalogservice.dtos.BuyTicketPaymentDTO
import it.polito.wa2.group22.ticketcatalogservice.dtos.UserProfileDTO
import it.polito.wa2.group22.ticketcatalogservice.entities.Order
import it.polito.wa2.group22.ticketcatalogservice.entities.Ticket
import it.polito.wa2.group22.ticketcatalogservice.services.TicketCatalogueService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

@RestController
class TicketCatalogueController(@Value("\${traveler-service-endpoint}") travelerServiceEndpoint: String) {

    private val webClient: WebClient = WebClient.create(travelerServiceEndpoint)

    @Autowired
    lateinit var ticketCatalogueService: TicketCatalogueService

    private val principal = ReactiveSecurityContextHolder.getContext()
        .map { it.authentication.principal as Long }

    @GetMapping("/admin/orders")
    fun getAllOrders(): Flow<Order> {
        return ticketCatalogueService.getAllOrders()
    }

    @GetMapping("/admin/orders/{userId}")
    suspend fun getAllUserOrdersAdmin(@PathVariable userId: Long): Flow<Order> {
        return ticketCatalogueService.getOrdersByUser(  userId)
    }

    @GetMapping("/tickets")
    suspend fun getAllTickets(): Flow<Ticket> {
        return ticketCatalogueService.getAllTickets()
    }

    @PostMapping("/admin/tickets")
    suspend fun addNewTicket(@RequestBody newTicket: Ticket): Flow<Ticket> {
        return ticketCatalogueService.createNewTicket(newTicket)
    }

    @PostMapping("/shop/{ticketId}")
    suspend fun buyTicket(
        @RequestHeader("Authorization") authorization: String?,
        @PathVariable ticketId: Long,
        @RequestBody paymentBuyInfo: BuyTicketPaymentDTO
    ): ResponseEntity<Order> {
        var userProfileDTO: UserProfileDTO?
        try {
            userProfileDTO = webClient
                .get()
                .uri("/my/profile")
                .header("Authorization", authorization)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .awaitBody()
        } catch (e: Exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }

        val userId = principal.awaitSingle()

        val ticket = ticketCatalogueService.getTicketById(ticketId)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).build()

        // Check if the user is eligible
        val dateOfBirth = LocalDate.parse(userProfileDTO.dateOfBirth, DateTimeFormatter.ISO_DATE)
        val age = (Period.between(LocalDate.now(), dateOfBirth).toTotalMonths()) / 12
        if (
            ((ticket.max_age != null) && (ticket.max_age < age)) || (((ticket.min_age != null) && (ticket.min_age > age)))
        ) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }

        val order = ticketCatalogueService.buyTicket(userId, ticketId, paymentBuyInfo)
        return ResponseEntity.status(HttpStatus.OK).body(order)
    }

    @GetMapping("/orders")
    suspend fun getAllUserOrders(): Flow<Order> {
        val userId = principal.awaitSingle()
        return ticketCatalogueService.getOrdersByUser(userId)
    }

    @GetMapping("/orders/{orderId}")
    suspend fun getOrderById(@PathVariable orderId: Long): Order? {
        val userId = principal.awaitSingle()
        return ticketCatalogueService.getOrderById(orderId, userId)
    }
}