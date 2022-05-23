package it.polito.wa2.group22.server.controllers

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import it.polito.wa2.group22.server.entities.UserDetails
import it.polito.wa2.group22.server.repositories.UserDetailsRepository
import it.polito.wa2.group22.server.services.AdminService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
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
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.*


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AdminControllerTests(@Value("\${jwt.key}") private val key: String) {

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
    lateinit var adminService: AdminService

    /*@BeforeAll
    fun setup(){
        val user1 = UserDetails(
            "TestUser1",
            "TestUser1",
            "TestAddress1",
            java.sql.Date.valueOf(LocalDate.of(1997,12,1)).toString(),
            "3364459928"
        )

        val user2 = UserDetails(
            "TestUser2",
            "TestUser2",
            "TestAddress2",
            java.sql.Date.valueOf(LocalDate.of(1997,12,1)).toString(),
            "3364459928"
        )

        userDetailsRepository.save(user1)
        userDetailsRepository.save(user2)
    }*/

    @Test
    fun getTravelerProfileValid() {
        val user= userDetailsRepository.save(UserDetails(
            "getTravelerProfileInvalid",
            "getTravelerProfileInvalid",
            "getUserProfileValidAddress",
            "21/07/1997",
            "3798415981"
        ))
        val headers = HttpHeaders()
        val tkn = Jwts.builder()
            .setSubject(user.username)
            .setIssuedAt(Date.from(Instant.now()))
            .setExpiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
            .claim("roles", listOf("ADMIN"))
            .signWith(Keys.hmacShaKeyFor(key.toByteArray())).compact()
        headers.set("Authorization", "Bearer $tkn")
        val requestEntity = HttpEntity<Unit>(headers)
        val url = "http://localhost:$port/admin/traveler/${user.username}/profile"
        val response = restTemplate.exchange(
            url, HttpMethod.GET, requestEntity, Any::class.java, Any::class.java
        )
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun getTravelerProfileInvalid() {
        val user= userDetailsRepository.save(UserDetails("getTravelerProfileInvalid",
            "getTravelerProfileInvalid",
            "getUserProfileValidAddress",
            "21/07/1997",
            "3798415981"
        ))
        val headers = HttpHeaders()
        val tkn = Jwts.builder()
            .setSubject(user.username)
            .setIssuedAt(Date.from(Instant.now()))
            .setExpiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
            .claim("roles", listOf("USER"))
            .signWith(Keys.hmacShaKeyFor(key.toByteArray())).compact()
        headers.set("Authorization", "Bearer $tkn")
        val requestEntity = HttpEntity<Unit>(headers)
        val url = "http://localhost:$port/admin/traveler/${user.username}/profile"
        val response = restTemplate.exchange(
            url, HttpMethod.GET, requestEntity, Any::class.java, Any::class.java
        )
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun getTravelerTicketsValid() {

        val user= userDetailsRepository.save(UserDetails("getTravelerTicketsValid","getTravelerTicketsValid"))
        val headers = HttpHeaders()
        val tkn = Jwts.builder()
            .setSubject(user.username)
            .setIssuedAt(Date.from(Instant.now()))
            .setExpiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
            .claim("roles", listOf("ADMIN"))
            .signWith(Keys.hmacShaKeyFor(key.toByteArray())).compact()

        headers.set("Authorization", "Bearer $tkn")
        val requestEntity = HttpEntity<Unit>(headers)
        val url = "http://localhost:$port/admin/traveler/${user.username}/tickets"
        val response = restTemplate.exchange(
            url, HttpMethod.GET, requestEntity, Any::class.java, Any::class.java
        )
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
    }

    @Test
    fun getTravelerTicketsInvalid() {
        val user= userDetailsRepository.save(UserDetails("getTravelerTicketsInvalid","getTravelerTicketsInvalid"))
        val headers = HttpHeaders()
        val tkn = Jwts.builder()
            .setSubject(user.username)
            .setIssuedAt(Date.from(Instant.now()))
            .setExpiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
            .claim("roles", listOf("USER"))
            .signWith(Keys.hmacShaKeyFor(key.toByteArray())).compact()
        headers.set("Authorization", "Bearer $tkn")
        val requestEntity = HttpEntity<Unit>(headers)
        val url = "http://localhost:$port/admin/traveler/${user.username}/tickets"
        val response = restTemplate.exchange(
            url, HttpMethod.GET, requestEntity, Any::class.java, Any::class.java
        )
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.statusCode)
    }

    @Test
    fun getTravelersValid() {
        val user = userDetailsRepository.save(UserDetails(
            "TestUser1",
            "TestUser1",
            "TestAddress1",
            java.sql.Date.valueOf(LocalDate.of(1997,12,1)).toString(),
            "3364459928"
        ))
        val headers = HttpHeaders()
        val tkn = Jwts.builder()
            .setSubject(user.username)
            .setIssuedAt(Date.from(Instant.now()))
            .setExpiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
            .claim("roles", listOf("ADMIN"))
            .signWith(Keys.hmacShaKeyFor(key.toByteArray())).compact()
        headers.set("Authorization", "Bearer $tkn")
        val requestEntity = HttpEntity<Unit>(headers)
        val url = "http://localhost:$port/admin/travelers"
        val response = restTemplate.exchange(
            url, HttpMethod.GET, requestEntity, Any::class.java, Any::class.java
        )
        Assertions.assertEquals(HttpStatus.OK, response.statusCode)
    }


}