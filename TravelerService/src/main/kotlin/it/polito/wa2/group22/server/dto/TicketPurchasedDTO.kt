package it.polito.wa2.group22.server.dto

import it.polito.wa2.group22.server.entities.TicketPurchased
import java.sql.Timestamp

data class TicketPurchasedDTO(
    var sub: Int?,
    var iat: Timestamp,
    var exp: Timestamp,
    var zid: String,
    var jws: String
)

fun TicketPurchased.toDTO(): TicketPurchasedDTO{
    return TicketPurchasedDTO(sub, iat, exp, zid, jws)
}