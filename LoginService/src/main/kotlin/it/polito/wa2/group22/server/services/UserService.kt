package it.polito.wa2.group22.server.services

import it.polito.wa2.group22.server.dto.ActivationDTO
import it.polito.wa2.group22.server.dto.UserDTO
import it.polito.wa2.group22.server.dto.UserDetailsDTO
import it.polito.wa2.group22.server.dto.toDTO
import it.polito.wa2.group22.server.entities.Activation
import it.polito.wa2.group22.server.entities.User
import it.polito.wa2.group22.server.exceptions.EmailServiceException
import it.polito.wa2.group22.server.interfaces.*
import it.polito.wa2.group22.server.repositories.ActivationRepository
import it.polito.wa2.group22.server.repositories.UserRepository
import it.polito.wa2.group22.server.utils.RegistrationResult
import it.polito.wa2.group22.server.utils.ValidationResult

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.MailException
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.Date

@Service
class UserService: UserDetailsService{

    @Autowired
    lateinit var emailService: EmailService

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var activationRepository: ActivationRepository

   @Autowired
   lateinit var bCryptPasswordEncoder: BCryptPasswordEncoder

    private var codeLength = 15
    private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    private val passwordRegularExp =
        Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
    private val emailRegularExp =
        Regex("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$")

    // new user registration
    fun userRegister(userDto: UserDTO): RegisterResponse {
        val userSaved: User?
        val user = User(userDto.nickname, userDto.password, userDto.email)
        var userRegistrationResult = isValidUser(user)
        if (userRegistrationResult != RegistrationResult.VALID_USER) {
            return RegisterResponseError(userRegistrationResult)
        }
        //user.salt = BCrypt.gensalt(10)
        //user.password = BCrypt.hashpw(user.password, user.salt)
        user.password = bCryptPasswordEncoder.encode(user.password)
        var pass = bCryptPasswordEncoder.encode(user.password)


        // TODO: Chiedere al prof se gestito bene
        try {
            userSaved = userRepository.save(user)
        } catch (e: Exception) {
            userRegistrationResult = RegistrationResult.DB_ERROR
            println("Error in database")
            e.printStackTrace()
            return RegisterResponseError(userRegistrationResult)
        }

        val activationDTOResult: ActivationDTO = newActivation(userSaved)
            ?: return RegisterResponseError(userRegistrationResult)
        return RegisterResponseValid(activationDTOResult.provisional_id, activationDTOResult.email!!)
    }

    fun newActivation(user: User): ActivationDTO? {
        val activation: Activation?

        // TODO: Chiedere al prof se gestito bene
        try {
            activation = activationRepository.save(Activation(user, generateCode()))
        } catch (e: Exception){
            e.printStackTrace()
            return null
        }

        try {
            emailService.sendMail(
                activation.user.email,
                activation.user.username,
                activation.activationCode,
                activation.expDate
            )
        } catch (e: MailException) {
            println("Error in mail configuration")
            e.printStackTrace()
            activationRepository.delete(activation)
        } catch (e: EmailServiceException) {
            println("Error in mail service: ${e.message}")
            activationRepository.delete(activation)
        }

        return activation.toDTO()
    }

    private fun generateCode(): String {
        return (1..codeLength)
            .map { kotlin.random.Random.nextInt(0, charPool.size) }
            .map { charPool[it] }
            .joinToString("")
    }

    fun userValidate(request: ActivationDTO): ValidationResponse {
        //check field of the request
        if (request.activation_code.isNullOrBlank() or request.provisional_id.toString().isBlank())
            return ValidationResponseError(ValidationResult.INVALID_REQUEST)

        //take the activation from the DB
        val activation: Activation = activationRepository.findById(request.provisional_id).orElse(null)
            ?: return ValidationResponseError(ValidationResult.NOT_VALID_ID)

        //check activation code validity
        if (activation.activationCode != request.activation_code) {
            activation.attempt--
            if (activation.attempt == 0) {
                // delete user and relative activation
                userRepository.delete(activation.user)
                return ValidationResponseError(ValidationResult.LIMIT_ATTEMPT)
            }
            try {
                activationRepository.save(activation)
            } catch (e: Exception) {
                println("Error in database")
                e.printStackTrace()
            }
            return ValidationResponseError(ValidationResult.NOT_VALID_ACTIVATION_CODE)
        }

        //Check expiration date
        if (activation.expDate.before(Date.from(Instant.now())))
            return ValidationResponseError(ValidationResult.EXPIRED_VALIDATION)

        //all the checks passed
        val modification = activation.user
        modification.isActive = true

        try {
            userRepository.save(modification)
        } catch (e: Exception) {
            println("Error in database")
            e.printStackTrace()
        }
        activationRepository.delete(activation)
        return ValidationResponseValid(activation.user.id!!, activation.user.username, activation.user.email)
    }

    fun isValidUser(user: User): RegistrationResult {
        return when {
            // username, password, and email address cannot be empty;
            user.username.isBlank() -> RegistrationResult.BLANK_USERNAME
            user.password.isBlank() -> RegistrationResult.BLANK_PASSWORD
            user.email.isBlank() -> RegistrationResult.BLANK_EMAIL
            // username and email address must be unique system-wide;
            userRepository.findByUsername(user.username) != null -> RegistrationResult.USERNAME_IS_NOT_UNIQUE
            userRepository.findByEmail(user.email) != null  -> RegistrationResult.EMAIL_IS_NOT_UNIQUE
            // validation password and email
            !user.email.matches(emailRegularExp) -> RegistrationResult.INVALID_EMAIL
            !user.password.matches(passwordRegularExp) -> RegistrationResult.INVALID_PASSWORD
            else -> RegistrationResult.VALID_USER
        }
    }

    // Time Scheduled Prune: fixed at 1 hour
    @Scheduled(fixedDelay = 3600000)
    fun pruneInactive() {
        val filteredAct = activationRepository
            .getInactiveActivations()
            .filter { act -> act.expDate.before(Date()) }
            .map { act -> act.user.id }
        userRepository.deleteAllById(filteredAct)
    }

    override fun loadUserByUsername(username: String?): UserDetails {
        var user = userRepository.findByUsername(username!!)
        if (user == null) {
            throw UsernameNotFoundException("User not found in the database")
        }
        println(user.username)
        return UserDetailsDTO(user)
    }
}