package it.polito.wa2.group22.fakebankservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BankServiceMockApplication

fun main(args: Array<String>) {
	runApplication<BankServiceMockApplication>(*args)
}
