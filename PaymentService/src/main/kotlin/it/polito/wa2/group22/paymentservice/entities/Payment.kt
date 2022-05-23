package it.polito.wa2.group22.paymentservice.entities

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import java.util.UUID

data class Payment(
    @Id
    @Column("paymentid")
    val payment : Long?,

    @Column("orderid")
    val orderID:Long,

    @Column("userid")
    val userId : String,

    /**
     *  0 = pending
     *  1 = accepted
     *  2 = denied
     */

    @Column("status")
    var status: Int

)
