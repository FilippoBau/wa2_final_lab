package it.polito.wa2.group22.ticketcatalogservice.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table("tickets")
data class Ticket(
    @Id
    val id: Long,
    val price: Float,
    val type: String,
    val max_age: Int?,
    val min_age: Int?
)