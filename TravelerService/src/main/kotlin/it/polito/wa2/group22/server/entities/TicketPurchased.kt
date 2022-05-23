package it.polito.wa2.group22.server.entities

import java.sql.Timestamp
import javax.persistence.*

@Entity
@Table(name = "ticket_purchased")
class TicketPurchased(
    var iat: Timestamp,
    var exp: Timestamp,
    var zid: String,
    var jws: String,
    @ManyToOne
    @JoinColumn(name = "user_details_username")
    var userDetails: UserDetails? = null
) {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var sub: Int? = null
}