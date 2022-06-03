package it.polito.wa2.group22.ticketcatalogservice.security

import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import it.polito.wa2.group22.ticketcatalogservice.dtos.UserDetailsDTO
import it.polito.wa2.group22.ticketcatalogservice.utils.listStringToListRole
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Base64

@Component
class JWTUtils(
    @Value("\${jwt.key}") private val key: String
) {

    private val parser: JwtParser =
        Jwts.parserBuilder().setSigningKey(Base64.getEncoder().encodeToString(key.toByteArray())).build()

    fun validateJwt(authToken: String): Boolean {
        try {
            val jwtParsed = parser.parseClaimsJws(authToken).body

            val user = jwtParsed.getValue("sub").toString()
            val roles = listStringToListRole(
                jwtParsed.getValue("roles") as List<String>
            )

            if (user.isBlank() || roles.isEmpty() || roles.any { r -> r.equals("USER") || r.equals("ADMIN") })
                return false
            return true
        } catch (e: Exception) {
            println(e.message)
            return false
        }
    }


    fun getDetailsJwt(authToken: String): UserDetailsDTO {
        val jwtParsed = parser.parseClaimsJws(authToken)

        val user = jwtParsed.body.getValue("sub").toString()
        val roles = listStringToListRole(
            jwtParsed.body.getValue("roles") as List<String>
        )
        return UserDetailsDTO(user, roles)
    }

}