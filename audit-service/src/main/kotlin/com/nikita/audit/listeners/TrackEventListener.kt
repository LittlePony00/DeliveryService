package com.nikita.audit.listeners

import com.nikita.events.RatedEvent.CourierRatedEvent
import com.nikita.events.events.DeliveryEvent
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.*
import org.springframework.amqp.support.AmqpHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap


@Component
class DeliveryEventListener {

    private val processedOrderCreations: MutableSet<Long> = ConcurrentHashMap.newKeySet()

    @RabbitListener(
        bindings = [
            QueueBinding(
                value = Queue(
                    name = QUEUE_NAME,
                    durable = "true",
                    arguments = [
                        Argument(name = "x-dead-letter-exchange", value = DLX_EXCHANGE),
                        Argument(name = "x-dead-letter-routing-key", value = DLQ_ROUTING_KEY)
                    ]
                ),
                exchange = Exchange(
                    name = EXCHANGE_NAME,
                    type = "topic",
                    durable = "true"
                ),
                key = [ROUTING_KEY_ORDER_CREATED]
            )
        ]
    )
    fun handleOrderCreatedEvent(
        @Payload event: DeliveryEvent.OrderCreatedEvent,
        channel: com.rabbitmq.client.Channel,
        @Header(AmqpHeaders.DELIVERY_TAG) deliveryTag: Long
    ) {
        try {
            if (!processedOrderCreations.add(event.orderId)) {
                log.warn("Duplicate event received for orderId: {}", event.orderId)
                channel.basicAck(deliveryTag, false)
                return
            }

            log.info("Received OrderCreatedEvent: $event")

            if (event.recipientName.equals("CRASH", ignoreCase = true)) {
                throw RuntimeException("Simulating error for DLQ test")
            }

            log.info("Notification sent: New order created for '${event.recipientName}' to address '${event.deliveryAddress}'!")

            // channel.basicAck(deliveryTag, false)

        } catch (e: Exception) {
            log.error("Failed to process event: $event. Sending to DLQ.", e)
            channel.basicNack(deliveryTag, false, false)
        }
    }

    @RabbitListener(
        bindings = [
            QueueBinding(
                value = Queue(name = QUEUE_NAME, durable = "true"),
                exchange = Exchange(name = EXCHANGE_NAME, type = "topic", durable = "true"),
                key = [ROUTING_KEY_COURIER_ASSIGNED]
            )
        ]
    )
    fun handleCourierAssignedEvent(
        @Payload event: DeliveryEvent.CourierAssignedEvent,
        channel: com.rabbitmq.client.Channel,
        @Header(AmqpHeaders.DELIVERY_TAG) deliveryTag: Long
    ) {
        try {
            log.info("Received CourierAssignedEvent: $event")
            log.info("Notification sent: Courier ${event.courierName} assigned to order ${event.orderId}")
            channel.basicAck(deliveryTag, false)
        } catch (e: Exception) {
            log.error("Failed to process event: $event", e)
            channel.basicNack(deliveryTag, false, false)
        }
    }

    @RabbitListener(
        bindings = [
            QueueBinding(
                value = Queue(name = QUEUE_NAME, durable = "true"),
                exchange = Exchange(name = EXCHANGE_NAME, type = "topic", durable = "true"),
                key = [ROUTING_KEY_DELIVERY_STARTED]
            )
        ]
    )
    fun handleDeliveryStartedEvent(
        @Payload event: DeliveryEvent.DeliveryStartedEvent,
        channel: com.rabbitmq.client.Channel,
        @Header(AmqpHeaders.DELIVERY_TAG) deliveryTag: Long
    ) {
        try {
            log.info("Received DeliveryStartedEvent: $event")
            log.info("Notification sent: Delivery started for order ${event.orderId}. Current location: ${event.currentLocation}")
            channel.basicAck(deliveryTag, false)
        } catch (e: Exception) {
            log.error("Failed to process event: $event", e)
            channel.basicNack(deliveryTag, false, false)
        }
    }

    @RabbitListener(
        bindings = [
            QueueBinding(
                value = Queue(name = QUEUE_NAME, durable = "true"),
                exchange = Exchange(name = EXCHANGE_NAME, type = "topic", durable = "true"),
                key = [ROUTING_KEY_DELIVERY_COMPLETED]
            )
        ]
    )
    fun handleDeliveryCompletedEvent(
        @Payload event: DeliveryEvent.DeliveryCompletedEvent,
        channel: com.rabbitmq.client.Channel,
        @Header(AmqpHeaders.DELIVERY_TAG) deliveryTag: Long
    ) {
        try {
            log.info("Received DeliveryCompletedEvent: $event")
            log.info("Notification sent: Delivery completed for order ${event.orderId}")
            channel.basicAck(deliveryTag, false)
        } catch (e: Exception) {
            log.error("Failed to process event: $event", e)
            channel.basicNack(deliveryTag, false, false)
        }
    }

    @RabbitListener(
        bindings = [
            QueueBinding(
                value = Queue(
                    name = QUEUE_NAME,
                    durable = "true",
                    arguments = [
                        Argument(name = "x-dead-letter-exchange", value = DLX_EXCHANGE),
                        Argument(name = "x-dead-letter-routing-key", value = DLQ_ROUTING_KEY)
                    ]
                ),
                exchange = Exchange(
                    name = EXCHANGE_NAME,
                    type = "topic",
                    durable = "true"
                ),
                key = [ROUTING_KEY_DELIVERY_CANCELLED]
            )
        ]
    )
    fun handleDeliveryCancelledEvent(
        @Payload event: DeliveryEvent.DeliveryCancelledEvent,
        channel: com.rabbitmq.client.Channel,
        @Header(AmqpHeaders.DELIVERY_TAG) deliveryTag: Long
    ) {
        try {
            log.info("Received DeliveryCancelledEvent: $event")
            log.info("Notification sent: Order ${event.orderId} cancelled!")
            channel.basicAck(deliveryTag, false)

        } catch (e: Exception) {
            log.error("Failed to process event: $event. Sending to DLQ.", e)
            channel.basicNack(deliveryTag, false, false)
        }
    }

    @RabbitListener(
        bindings = [
            QueueBinding(
                value = Queue(
                    name = DLQ_QUEUE_NAME,
                    durable = "true"
                ),
                exchange = Exchange(
                    name = DLX_EXCHANGE,
                    type = "topic",
                    durable = "true"
                ),
                key = [DLQ_ROUTING_KEY]
            )
        ]
    )
    fun handleDlqMessages(failedMessage: Any) {
        log.error("!!! Received message in DLQ: {}", failedMessage)
        // Логика оповещения администраторов
    }

    @RabbitListener(
        bindings = [QueueBinding(
            value = Queue(name = RATING_QUEUE_NAME, durable = "true"),
            exchange = Exchange(name = RATING_EXCHANGE_NAME, type = "fanout")
        )]
    )
    fun handleCourierRating(event: CourierRatedEvent) {
        log.info("NOTIFY: Sending notification. Courier {} received rating {} for order {}", event.courierId, event.score, event.orderId)
    }

    companion object {
        private val log = LoggerFactory.getLogger(DeliveryEventListener::class.java)

        const val EXCHANGE_NAME = "delivery-exchange"
        const val QUEUE_NAME = "notification-queue"

        const val ROUTING_KEY_ORDER_CREATED = "order.created"
        const val ROUTING_KEY_COURIER_ASSIGNED = "courier.assigned"
        const val ROUTING_KEY_DELIVERY_STARTED = "delivery.started"
        const val ROUTING_KEY_DELIVERY_COMPLETED = "delivery.completed"
        const val ROUTING_KEY_DELIVERY_CANCELLED = "delivery.cancelled"

        const val DLQ_QUEUE_NAME = "notification-queue.dlq"
        const val DLX_EXCHANGE = "dlx-exchange"
        const val DLQ_ROUTING_KEY = "dlq.notifications"

        const val RATING_QUEUE_NAME = "q.audit.analytics"
        const val RATING_EXCHANGE_NAME = "analytics-fanout"
    }
}
