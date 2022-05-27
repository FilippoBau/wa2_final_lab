package it.polito.wa2.group22.ticketcatalogservice.services

import it.polito.wa2.group22.ticketcatalogservice.dtos.BuyTicketPaymentDTO
import it.polito.wa2.group22.ticketcatalogservice.entities.Order
import it.polito.wa2.group22.ticketcatalogservice.entities.PaymentReq
import it.polito.wa2.group22.ticketcatalogservice.entities.Ticket
import it.polito.wa2.group22.ticketcatalogservice.kafka.Topics
import it.polito.wa2.group22.ticketcatalogservice.repositories.OrderRepository
import it.polito.wa2.group22.ticketcatalogservice.repositories.TicketRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.springframework.messaging.Message
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service

@Service
class TicketCatalogueService(
    @Autowired
    @Qualifier("paymentRequestTemplate")
    private val paymentRequestTemplate: KafkaTemplate<String, Any>
) {
    @Autowired
    lateinit var orderRepository: OrderRepository

    @Autowired
    lateinit var ticketRepository: TicketRepository

    fun getAllOrders(): Flow<Order> {
        return orderRepository.findAllOrders()
    }

    fun getAllTickets(): Flow<Ticket> {
        return ticketRepository.findAll()
    }

    suspend fun createNewTicket(ticket: Ticket): Flow<Ticket> {
        val savedTicket = ticketRepository.save(ticket)
        return flow {
            emit(savedTicket)
        }
    }

    suspend fun getOrderById(orderId: Long, userId: Long): Order? {
        return orderRepository.findOrderById(orderId, userId)
    }

    fun getOrdersByUser(id: Long): Flow<Order> {
        return orderRepository.findOrdersByUser(id)
    }

    suspend fun getTicketById(id: Long): Ticket? {
        return ticketRepository.findTicketById(id)
    }

    suspend fun buyTicket(userId: Long, ticketId: Long, buyTicketPaymentDTO: BuyTicketPaymentDTO) : Order {

        val order = orderRepository.save(
            Order(
                null, ticketId, buyTicketPaymentDTO.amount, userId, "PENDING", null, null
            )
        )

        val message: Message<PaymentReq> = MessageBuilder
            .withPayload(PaymentReq(
                order.id!!,
                userId,
                buyTicketPaymentDTO.paymentInformations.creditCardNumber,
                buyTicketPaymentDTO.paymentInformations.cvv,
                buyTicketPaymentDTO.paymentInformations.expirationDate,
                buyTicketPaymentDTO.amount,
            ))
            .setHeader(KafkaHeaders.TOPIC, Topics.paymentToTicketCatalogue)
            .build()

        paymentRequestTemplate.send(message)

        return order
    }
}