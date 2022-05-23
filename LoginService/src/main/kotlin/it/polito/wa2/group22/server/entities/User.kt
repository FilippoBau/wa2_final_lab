package it.polito.wa2.group22.server.entities

import it.polito.wa2.group22.server.utils.Role
import java.util.*
import javax.persistence.*

@Entity
@Table(name = "users")
class User(
    var username: String = "",
    var password: String = "",
    var email: String = ""
) {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_generator")
    @SequenceGenerator(
        name = "user_generator",
        sequenceName = "sequence_1",
        initialValue = 1,
        allocationSize = 1
    )
    @Column(name = "id")
    var id: Long? = null

    var salt: String = ""
    var isActive: Boolean = false

    @OneToOne(mappedBy = "user", cascade = [CascadeType.ALL])
    var activation: Activation? = null

    var role: Role = Role.CUSTOMER
}