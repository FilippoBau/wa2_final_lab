package it.polito.wa2.group22.ticketcatalogservice.dtos

import it.polito.wa2.group22.ticketcatalogservice.entities.PaymentInfo

data class BuyTicketPaymentDTO(
    val amount: Int,
    val creditCardNumber: String,
    val cvv: String,
    val expirationDate: String
)
