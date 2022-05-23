package it.polito.wa2.group22.server.repositories

import it.polito.wa2.group22.server.entities.Activation
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface ActivationRepository : CrudRepository<Activation, UUID> {
    fun findByActivationCode(activationCode: String): Activation?;
    /*@Query(
        "SELECT * FROM activations AS A, users AS US WHERE A.user_id IN (SELECT id FROM users WHERE is_active = false)",
        nativeQuery = true
    )*/
    @Query(
        "SELECT * FROM activations AS A, users AS U WHERE A.user_id = U.id AND U.is_active = false",
        nativeQuery = true
    )
    fun getInactiveActivations(): List<Activation>
}