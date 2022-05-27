package it.polito.wa2.group22.ticketcatalogservice.entities

import com.fasterxml.jackson.annotation.JsonProperty

data class PaymentReq(
    @JsonProperty("orderId")
    val orderId: Long,

    @JsonProperty("userId")
    val userId: Long,

    @JsonProperty("creditCardNumber")
    val creditCardNumber: Int,

    @JsonProperty("cvv")
    val cvv: Int,

    @JsonProperty("expirationDate")
    val expirationDate: String,

    @JsonProperty("amount")
    val amount: Int
)