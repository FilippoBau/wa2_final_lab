package it.polito.wa2.group22.ticketcatalogservice.kafka.consumers

import it.polito.wa2.group22.ticketcatalogservice.entities.PaymentRes
import it.polito.wa2.group22.ticketcatalogservice.kafka.Topics
import it.polito.wa2.group22.ticketcatalogservice.repositories.OrderRepository
import kotlinx.coroutines.runBlocking
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component


@Component
class PaymentConsumerResponse {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    lateinit var ordersRepository: OrderRepository


    @KafkaListener(
        containerFactory = "paymentResponseListenerContainerFactory",
        topics = [Topics.paymentToTicketCatalogue],
        groupId = "ctl"
    )
    fun listenFromPaymentService(consumerRecord: ConsumerRecord<Any, PaymentRes>) {

        logger.info("Incoming payment {}", consumerRecord)

        val response = consumerRecord.value()

        runBlocking {
            // Update the corresponding order in the database
            val targetOrder = ordersRepository.findOrderById(response.orderId)
            if (targetOrder != null) {
                logger.info("Received payment response for order {}, status = {}", response.orderId, response.status)
                targetOrder.status = if (response.status == 1) "COMPLETED" else "ERROR"
                ordersRepository.save(targetOrder)
            }
            else {
                logger.error("Received payment response for non-existing order {}", response.orderId)
            }
        }

    }
}