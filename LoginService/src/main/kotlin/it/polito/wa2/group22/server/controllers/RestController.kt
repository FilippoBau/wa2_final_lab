package it.polito.wa2.group22.server.controllers

import it.polito.wa2.group22.server.dto.ActivationDTO
import it.polito.wa2.group22.server.dto.UserDTO
import it.polito.wa2.group22.server.interfaces.*
import it.polito.wa2.group22.server.services.UserService
import it.polito.wa2.group22.server.utils.RegistrationResult
import it.polito.wa2.group22.server.utils.ValidationResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController


@RestController
class RestController {

    @Autowired
    lateinit var userService: UserService

    @PostMapping("/user/register")
    fun registerUser(@RequestBody reqBody: UserDTO): ResponseEntity<RegisterResponse> {
        val result = userService.userRegister(reqBody)

        return if (result is RegisterResponseValid) {
            ResponseEntity.status(HttpStatus.ACCEPTED).body(result)
        } else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result)
        }
    }

    @PostMapping("/user/validate")
    fun validateUser(@RequestBody reqBody: ActivationDTO): ResponseEntity<ValidationResponse> {
        val result = userService.userValidate(reqBody)

        return if (result is ValidationResponseValid) {
            ResponseEntity.status(HttpStatus.OK).body(result)
        } else {
            println("Eh boh")
            ResponseEntity.status(HttpStatus.NOT_FOUND).body(result)
        }
    }
}