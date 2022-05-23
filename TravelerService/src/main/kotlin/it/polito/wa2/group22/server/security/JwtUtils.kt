package it.polito.wa2.group22.server.security

import io.jsonwebtoken.Jwt
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import it.polito.wa2.group22.server.dto.UserDetailsDTO
import it.polito.wa2.group22.server.utils.Role
import it.polito.wa2.group22.server.utils.listStringToListRole
import org.apache.tomcat.util.codec.binary.Base64
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

//TODO: completare

@Component
class JwtUtils(@Value("\${jwt.key}") private val key: String){

    private val parser: JwtParser =
        Jwts.parserBuilder().setSigningKey(Base64.encodeBase64String(key.toByteArray())).build()

    /*val allowedRoles = listOf(SimpleGrantedAuthority(Role.ADMIN.toString()),
                              SimpleGrantedAuthority(Role.CUSTOMER.toString()))*/

    //TODO: decidere se validare solamente

    fun validateJwt(authToken: String): Boolean {
        return try {
            val jwtParsed = parser.parseClaimsJws(authToken)
            true
        } catch (e: Exception) {
            println(e.message)
            return false
        }
    }
    //TODO: decidere se validare
    fun getDetailsJwt(authToken: String): UserDetailsDTO? {
        try {
            if (!validateJwt(authToken)) {
                return null
            }
            var jwtParsed = parser.parseClaimsJws(authToken)
            val user = jwtParsed.body.getValue("sub").toString()
            val roles = listStringToListRole(
                jwtParsed.body.getValue("roles") as List<String>
            )
            return UserDetailsDTO(username = user, roles = roles)
        } catch (e: Exception){
            return null
        }
    }
}