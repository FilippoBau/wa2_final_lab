package it.polito.wa2.group22.server.controllers

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import it.polito.wa2.group22.server.dto.UserProfileDTO
import it.polito.wa2.group22.server.entities.UserDetails
import it.polito.wa2.group22.server.repositories.UserDetailsRepository
import it.polito.wa2.group22.server.services.UserService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MyControllerTests(@Value("\${jwt.key}") private val key: String) {

    companion object {
        @Container
        val postgres = PostgreSQLContainer<Nothing>("postgres:latest")

        @JvmStatic
        @DynamicPropertySource
        fun properties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
            registry.add("spring.jpa.hibernate.ddl-auto") { "create-drop" }
        }
    }


    @LocalServerPort
    var port: Int = 0

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Autowired
    lateinit var userDetailsRepository: UserDetailsRepository

    @Autowired
    lateinit var userService: UserService

    fun generateToken(username: String, exp: Date): String{
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(Date.from(Instant.now()))
            .setExpiration(exp)
            .claim("roles", listOf("USER"))
            .signWith(Keys.hmacShaKeyFor(key.toByteArray())).compact()
    }

    @Test
    fun getUserProfileValid() {
        val user = userDetailsRepository.save(
            UserDetails(
                "getUserProfileValid",
                "getUserProfileValidName",
                "getUserProfileValidAddress",
                "21/07/1997",
                "3798415981"
            ))

        val headers = HttpHeaders()
        val token = generateToken(user.username, Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
        headers.set("Authorization", "Bearer $token")
        val requestEntity = HttpEntity<Unit>(headers)
        val url = "http://localhost:$port/my/profile/"
        val response = restTemplate.exchange(
            url, HttpMethod.GET, requestEntity, Any::class.java, Any::class.java
        )
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun getUserProfileExpirationToken() {
        val user = userDetailsRepository.save(
            UserDetails(
                "getUserProfileExpirationToken",
                "getUserProfileValidName",
                "getUserProfileValidAddress",
                "21/07/1997",
                "3798415981"
            ))

        val headers = HttpHeaders()
        val token = generateToken(user.username, Date.from(Instant.now().minus(1, ChronoUnit.HOURS)))
        headers.set("Authorization", "Bearer $token")
        val requestEntity = HttpEntity<Unit>(headers)
        val url = "http://localhost:$port/my/profile/"
        val response = restTemplate.exchange(
            url, HttpMethod.GET, requestEntity, Any::class.java, Any::class.java
        )
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun getUserProfileNotValidTokenSignature() {
        val user = userDetailsRepository.save(
            UserDetails(
                "getUserProfileNotValidTokenSignature",
                "getUserProfileValidName",
                "getUserProfileValidAddress",
                "21/07/1997",
                "3798415981"
            ))

        val headers = HttpHeaders()
        val token = generateToken(user.username, Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
        headers.set("Authorization", "Bearer ${token}xxx")
        val requestEntity = HttpEntity<Unit>(headers)
        val url = "http://localhost:$port/my/profile/"
        val response = restTemplate.exchange(
            url, HttpMethod.GET, requestEntity, Any::class.java, Any::class.java
        )
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun getUserProfileNotValidUsername() {

        val headers = HttpHeaders()
        val token = generateToken("userName", Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
        headers.set("Authorization", "Bearer $token")
        val requestEntity = HttpEntity<Unit>(headers)
        val url = "http://localhost:$port/my/profile/"
        val response = restTemplate.exchange(
            url, HttpMethod.GET, requestEntity, Any::class.java, Any::class.java
        )
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    @Test
    fun putUserProfileValid() {
        val user = userDetailsRepository.save(
            UserDetails("putUserProfileValid", null))

        val body = UserProfileDTO(
            "name",
            "address",
            "21/07/1997",
            "3798415981"
        )

        val headers = HttpHeaders()
        val token = generateToken(user.username, Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
        headers.set("Authorization", "Bearer $token")
        val requestEntity = HttpEntity(body,headers)
        val url = "http://localhost:$port/my/profile/"
        val response = restTemplate.exchange(
            url, HttpMethod.POST, requestEntity, Any::class.java, Any::class.java
        )
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun putUserProfileFailed() {

        val body = UserProfileDTO(
            "name",
            "address",
            "21/07/1997",
            "3798415981"
        )

        val headers = HttpHeaders()
        val token = generateToken("userName", Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
        headers.set("Authorization", "Bearer $token")
        val requestEntity = HttpEntity(body,headers)
        val url = "http://localhost:$port/my/profile/"
        val response = restTemplate.exchange(
            url, HttpMethod.POST, requestEntity, Any::class.java, Any::class.java
        )
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.statusCode)
    }

    //GetMyTickets

   @Test
    fun getMyTicketsValid(){
        val user = userDetailsRepository.save(
            UserDetails(
                "getTicketsValid",
                "getTicketsValidName",
                "getTicketsValidAddress",
                "21/07/1997",
                "3798415981"
            ))
        userService.buyTickets(user.username, Action("buy_tickets", 1, "1,2,3"))
        val headers = HttpHeaders()
        val token = generateToken(user.username, Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
        headers.set("Authorization", "Bearer $token")
        val requestEntity = HttpEntity<Unit>(headers)
        val url = "http://localhost:$port/my/tickets/"
        val response = restTemplate.exchange(
            url, HttpMethod.GET, requestEntity, Any::class.java, Any::class.java
        )
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
    }

    //PostMyTickets

    @Test
    fun postMyTicketsValid(){
        val user = userDetailsRepository.save(
            UserDetails(
                "postTicketsValid",
                "postTicketsValidName",
                "postTicketsValidAddress",
                "21/07/1997",
                "3798415981"
            ))

        val headers = HttpHeaders()
        val token = generateToken(user.username, Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
        headers.set("Authorization", "Bearer $token")
        val requestEntity = HttpEntity<Action>(
            Action("buy_tickets", 3, "ABC"),
            headers
        )
        val url = "http://localhost:$port/my/tickets/"
        val response = restTemplate.exchange(
            url, HttpMethod.POST, requestEntity, Any::class.java, Any::class.java
        )
        println(response)
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
    }


    //GetMyTickets

    @Test
    fun getMyTicketsInvalid(){
        val user = userDetailsRepository.save(
            UserDetails(
                "getTicketsinValid",
                "getTicketsValidName",
                "getTicketsValidAddress",
                "21/07/1997",
                "3798415981"
            ))

        val headers = HttpHeaders()
        val token = generateToken(user.username, Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
        headers.set("Authorization", "Bearer ${token}XXX")
        val requestEntity = HttpEntity<Unit>(headers)
        val url = "http://localhost:$port/my/tickets/"
        val response = restTemplate.exchange(
            url, HttpMethod.GET, requestEntity, Any::class.java, Any::class.java
        )
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    //PostMyTickets

    @Test
    fun postMyTicketsInvalid(){
        val user = userDetailsRepository.save(
            UserDetails(
                "postTicketsinValid",
                "postTicketsValidName",
                "postTicketsValidAddress",
                "21/07/1997",
                "3798415981"
            ))

        val headers = HttpHeaders()
        val token = generateToken(user.username, Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
        headers.set("Authorization", "Bearer ${token}XXX")
        val requestEntity = HttpEntity<Unit>(headers)
        val url = "http://localhost:$port/my/tickets/"
        val response = restTemplate.exchange(
            url, HttpMethod.POST, requestEntity, Any::class.java, Any::class.java
        )
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }


}