package it.polito.wa2.group22.server.services

import it.polito.wa2.group22.server.controllers.Action
import it.polito.wa2.group22.server.dto.UserProfileDTO
import it.polito.wa2.group22.server.dto.toDTO
import it.polito.wa2.group22.server.entities.UserDetails
import it.polito.wa2.group22.server.repositories.TicketPurchasedRepository
import it.polito.wa2.group22.server.repositories.UserDetailsRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.sql.Date
import java.time.LocalDate


@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AdminServiceTests {

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
    lateinit var ticketPurchasedRepository: TicketPurchasedRepository

    @Autowired
    lateinit var adminService: AdminService

    @Autowired
    lateinit var userService: UserService

    @AfterEach
    fun dropData(){
        ticketPurchasedRepository.deleteAll()
        userDetailsRepository.deleteAll()
    }

    fun setupUsers(){
        val user1 = UserDetails(
            "TestUser1",
            "TestUser1",
            "TestAddress1",
            Date.valueOf(LocalDate.of(1997, 12, 1)).toString(),
            "3364459928"
        )

        val user2 = UserDetails(
            "TestUser2",
            "TestUser2",
            "TestAddress2",
            Date.valueOf(LocalDate.of(1997, 12, 1)).toString(),
            "3364459928"
        )

        userDetailsRepository.save(user1)
        userDetailsRepository.save(user2)
    }

    @Test
    fun getTravelersEmptyValid() {
        val users = adminService.getTravelers()
        assert(users.isEmpty())
    }

    @Test
    fun getTravelersValid() {
        setupUsers()
        val users = adminService.getTravelers()
        assert(users.size == 2)
        assert(
            users.contains(
                UserProfileDTO(
                    "TestUser1",
                    "TestAddress1",
                    Date.valueOf(LocalDate.of(1997, 12, 1)).toString(),
                    "3364459928"
                )
            )
        )
        assert(
            users.contains(
                UserProfileDTO(
                    "TestUser2",
                    "TestAddress2",
                    Date.valueOf(LocalDate.of(1997, 12, 1)).toString(),
                    "3364459928"
                )
            )
        )
    }



    @Test
    fun getTravelerTicketsValid() {
        val user = userDetailsRepository.save(UserDetails("getTravelerTicketsValid", "getTravelerTicketsValid"))
        val action = Action("buy_tickets", 1, "abc")
        userService.buyTickets(user.username,action)
        val res = adminService.getTravelerTickets(user.username)
        assert(res.size==1)
    }

    @Test
    fun getTravelerProfileValid() {
        val user = userDetailsRepository.save(UserDetails("getTravelerProfileValid", "getTravelerProfileValid"))
        val res = adminService.getTravelerProfile(user.username)
        Assertions.assertEquals(UserProfileDTO(user.name!!,user.address,user.date_of_birth,user.telephone_number), res)
    }
}