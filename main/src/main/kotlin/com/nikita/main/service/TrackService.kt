package com.immortalidiot.main.service

import com.immortalidiot.api.dto.PagedResponse
import com.immortalidiot.api.dto.OrderRequest
import com.immortalidiot.api.dto.OrderResponse
import com.immortalidiot.api.dto.DeliveryStatus
import com.immortalidiot.api.exception.ResourceNotFoundException
import com.immortalidiot.events.DeliveryEvent
import com.immortalidiot.main.config.RabbitMQConfig
import com.immortalidiot.main.storage.InMemoryRepository
import com.immortalidiot.main.websocket.DeliveryWebSocketPublisher
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service
import kotlin.math.ceil
import kotlin.math.min

interface DeliveryService {
    fun createOrder(orderRequest: OrderRequest): OrderResponse
    fun getOrderById(id: Long): OrderResponse
    fun getAllOrders(page: Int, size: Int): PagedResponse<OrderResponse>
    fun assignCourier(orderId: Long): OrderResponse
    fun startDelivery(orderId: Long): OrderResponse
    fun completeDelivery(orderId: Long): OrderResponse
    fun cancelOrder(id: Long): OrderResponse
}

@Service
internal class DeliveryServiceImpl(
    private val rabbitMqTemplate: RabbitTemplate,
    private val repository: InMemoryRepository,
    private val publisher: DeliveryWebSocketPublisher
) : DeliveryService {
    override fun createOrder(orderRequest: OrderRequest): OrderResponse {
        val newOrder = repository.createOrder(orderRequest)

        createOrderCreatedEvent(
            id = newOrder.id,
            deliveryAddress = newOrder.deliveryAddress,
            senderAddress = newOrder.senderAddress,
            recipientName = newOrder.recipientName
        )

        return newOrder
    }

    private fun createOrderCreatedEvent(id: Long, deliveryAddress: String, senderAddress: String, recipientName: String) {
        val event = DeliveryEvent.OrderCreatedEvent(
            orderId = id,
            deliveryAddress = deliveryAddress,
            senderAddress = senderAddress,
            recipientName = recipientName
        )

        rabbitMqTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY_ORDER_CREATED, event)
        publisher.sendEvent(event)
    }

    override fun getOrderById(id: Long): OrderResponse {
        val order = repository.getOrderById(id) ?: throw ResourceNotFoundException("Order", id)
        if (order.status == DeliveryStatus.CANCELLED) throw ResourceNotFoundException("Order", id)

        return order
    }

    override fun getAllOrders(page: Int, size: Int): PagedResponse<OrderResponse> {
        val allOrders = repository.getAllOrders()
            .sortedBy(OrderResponse::id)

        val totalElements = allOrders.size
        val totalPages = ceil(totalElements / size.toDouble()).toInt()
        val fromIndex = page * size
        val toIndex = min(fromIndex + size, totalElements)

        val content = if (fromIndex >= totalElements) emptyList()
        else allOrders.subList(fromIndex, toIndex).filter { order -> order.status != DeliveryStatus.CANCELLED }

        return PagedResponse(
            content = content,
            pageNumber = page,
            pageSize = size,
            totalElements = totalElements.toLong(),
            totalPages = totalPages,
            last = page >= totalPages - 1
        )
    }

    override fun assignCourier(orderId: Long): OrderResponse {
        val order = repository.assignCourier(orderId) ?: throw NoSuchElementException("Order with id $orderId not found")
        
        order.courierInfo?.let {
            val event = DeliveryEvent.CourierAssignedEvent(
                orderId = orderId,
                courierId = it.courierId!!,
                courierName = it.courierName!!
            )
            rabbitMqTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY_COURIER_ASSIGNED, event)
            publisher.sendEvent(event)
        }
        
        return order
    }

    override fun startDelivery(orderId: Long): OrderResponse {
        val order = repository.startDelivery(orderId) ?: throw NoSuchElementException("Order with id $orderId not found")
        
        order.courierInfo?.let {
            val event = DeliveryEvent.DeliveryStartedEvent(
                orderId = orderId,
                courierId = it.courierId!!,
                currentLocation = it.currentLocation ?: "Unknown"
            )
            rabbitMqTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY_DELIVERY_STARTED, event)
            publisher.sendEvent(event)
        }
        
        return order
    }

    override fun completeDelivery(orderId: Long): OrderResponse {
        val order = repository.completeDelivery(orderId) ?: throw NoSuchElementException("Order with id $orderId not found")
        
        order.courierInfo?.let {
            val event = DeliveryEvent.DeliveryCompletedEvent(
                orderId = orderId,
                courierId = it.courierId!!
            )
            rabbitMqTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY_DELIVERY_COMPLETED, event)
            publisher.sendEvent(event)
        }
        
        return order
    }

    override fun cancelOrder(id: Long): OrderResponse {
        val order = repository.cancelOrder(id) ?: throw NoSuchElementException("Order with id $id not found")

        createDeliveryCancelledEvent(order.id)

        return order
    }

    private fun createDeliveryCancelledEvent(id: Long) {
        val event = DeliveryEvent.DeliveryCancelledEvent(
            orderId = id,
            reason = "Cancelled by user"
        )

        rabbitMqTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY_DELIVERY_CANCELLED, event)
        publisher.sendEvent(event)
    }
}
