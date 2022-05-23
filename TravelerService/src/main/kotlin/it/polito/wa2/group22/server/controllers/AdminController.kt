package it.polito.wa2.group22.server.controllers

import it.polito.wa2.group22.server.services.AdminService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController

@RestController
class AdminController {


    @Autowired
    lateinit var adminService: AdminService

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/travelers")
    fun getTravelers(@RequestHeader("Authorization") jwt:String):ResponseEntity<Any>{
        return try {
            val body = adminService.getTravelers()
            ResponseEntity(body,HttpStatus.OK)
        }catch (e:Exception){
            ResponseEntity("${e.message}", HttpStatus.BAD_REQUEST)
        }

    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/traveler/{userID}/profile")
    fun getTravelerProfile(@PathVariable userID:String,@RequestHeader("Authorization") jwt:String):ResponseEntity<Any>{

    return try {
            val body = adminService.getTravelerProfile(userID)
            ResponseEntity(body,HttpStatus.OK)
        }catch (e:Exception){
            ResponseEntity("${e.message}", HttpStatus.BAD_REQUEST)
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/traveler/{userID}/tickets")
    fun getTravelerTickets(@PathVariable userID:String,@RequestHeader("Authorization") jwt:String):ResponseEntity<Any>{

        return try {
            val body=adminService.getTravelerTickets(userID)
            ResponseEntity(body,HttpStatus.OK)
        }catch (e:Exception){
            ResponseEntity("${e.message}", HttpStatus.BAD_REQUEST)
        }

    }
}