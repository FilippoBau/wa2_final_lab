package it.polito.wa2.group22.server.controllers

import it.polito.wa2.group22.server.dto.UserProfileDTO
import it.polito.wa2.group22.server.interfaces.UserServiceResponseValid
import it.polito.wa2.group22.server.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
class MyController {

    @Autowired
    lateinit var userService: UserService

    @GetMapping("my/profile")
    @PreAuthorize("hasRole('ROLE_USER')")
    fun getUserProfile(@RequestHeader("Authorization") jwt: String): ResponseEntity<Any> {
        //Retrieve the principal form the SecurityContext
        val principal = SecurityContextHolder.getContext().authentication.principal

        val res = userService.getUserProfile(principal.toString())
        return if (res is UserServiceResponseValid) {
            ResponseEntity.status(HttpStatus.OK).body(res.userDetails)
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res)
        }
    }


    @PostMapping("my/profile")
    @PreAuthorize("hasRole('ROLE_USER')")
    fun updateUserProfile(
        @RequestHeader("Authorization") jwt: String, @RequestBody reqBody: UserProfileDTO
    ): ResponseEntity<Any> {

        //Retrieve the principal form the SecurityContext
        val principal = SecurityContextHolder.getContext().authentication.principal
        val res = userService.updateUserProfile(reqBody, principal.toString())
        return if (res is UserServiceResponseValid) {
            ResponseEntity.status(HttpStatus.OK).body("")
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res)
        }
    }

    @GetMapping("my/tickets")
    @PreAuthorize("hasRole('ROLE_USER')")
    fun getUserTickets(@RequestHeader("Authorization") jwt: String): ResponseEntity<Any> {
        //Retrieve the principal form the SecurityContext
        val principal = SecurityContextHolder.getContext().authentication.principal
        val res = userService.getUserTickets(principal.toString())
        return if (res is UserServiceResponseValid) {
            ResponseEntity.status(HttpStatus.OK).body(res.tickets)
        } else{
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res)
        }
    }

    @PostMapping("my/tickets")
    @PreAuthorize("hasRole('ROLE_USER')")
    fun buyTickets(@RequestHeader("Authorization") jwt: String, @RequestBody reqBody: Action): ResponseEntity<Any> {
        val principal = SecurityContextHolder.getContext().authentication.principal
        val res = userService.buyTickets(principal.toString(), reqBody)
        return if (res is UserServiceResponseValid){
            ResponseEntity.status(HttpStatus.OK).body(res.tickets)
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res)
        }
    }
}

data class Action(val cmd : String, val quantity : Int, val zones : String)