package com.nikita.statistics.listeners

import com.nikita.events.events.DeliveryEvent
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.support.AmqpHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.stereotype.Component
import com.rabbitmq.client.Channel
import org.springframework.messaging.handler.annotation.Payload
import com.nikita.statistics.config.RabbitConfig

@Component
class DeliveryStatisticsListener {

    private val orderMap = mutableMapOf<Long, String>()
    private val deliveryStats = mutableMapOf<String, Int>()

    @RabbitListener(queues = [RabbitConfig.QUEUE_NAME])
    fun onOrderCreated(
        @Payload event: DeliveryEvent.OrderCreatedEvent,
        channel: Channel,
        @Header(AmqpHeaders.DELIVERY_TAG) tag: Long
    ) {
        try {
            orderMap[event.orderId] = event.recipientName
            println("📊 Total orders: ${orderMap.size}")
            println("📊 Statistics: New order to ${event.deliveryAddress}")

            channel.basicAck(tag, false)
        } catch (e: Exception) {
            channel.basicNack(tag, false, false)
            println("Failed to process message: ${e.message}")
        }
    }

    @RabbitListener(queues = [RabbitConfig.QUEUE_NAME_COMPLETED])
    fun onDeliveryCompleted(
        @Payload event: DeliveryEvent.DeliveryCompletedEvent,
        channel: Channel,
        @Header(AmqpHeaders.DELIVERY_TAG) tag: Long
    ) {
        try {
            val courierKey = "courier_${event.courierId}"
            deliveryStats[courierKey] = (deliveryStats[courierKey] ?: 0) + 1
            
            println("📊 Delivery completed for order ${event.orderId}")
            println("📊 Courier ${event.courierId} total deliveries: ${deliveryStats[courierKey]}")

            channel.basicAck(tag, false)
        } catch (e: Exception) {
            channel.basicNack(tag, false, false)
            println("Failed to process completed delivery: ${e.message}")
        }
    }
}
