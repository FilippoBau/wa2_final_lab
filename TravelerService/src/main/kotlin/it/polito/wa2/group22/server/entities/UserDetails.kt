package it.polito.wa2.group22.server.entities

import javax.persistence.*

@Entity
@Table(name = "user_details")
class UserDetails(
    //@Column(unique=true)
    @Id
    var username: String,
    var name: String? = null,
    var address: String? = null,
    var date_of_birth: String? = null,
    var telephone_number: String? = null
) {

    /*@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_generator")
    @SequenceGenerator(
        name = "user_generator",
        sequenceName = "sequence_1",
        initialValue = 1,
        allocationSize = 1
    )
    var id: Long? = null*/

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "userDetails")
    var tickets = mutableListOf<TicketPurchased>()

    fun addTicket(t: TicketPurchased): TicketPurchased {
        t.userDetails = this
        tickets.add(t)
        return t
    }

}
