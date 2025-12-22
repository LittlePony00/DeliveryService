package com.nikita.main.storage

import com.nikita.api.dto.OrderRequest
import com.nikita.api.dto.OrderResponse
import com.nikita.api.dto.DeliveryStatus
import com.nikita.api.dto.CourierInfo
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong
import kotlin.random.Random

@Component
internal class InMemoryRepository {
    private val orderIdSequence = AtomicLong(1)
    private val orders: MutableMap<Long, OrderResponse> = ConcurrentHashMap()
    
    private val availableCouriers = listOf(
        CourierInfo(1L, "Иван Иванов", "ул. Ленина, 10"),
        CourierInfo(2L, "Петр Петров", "пр. Победы, 25"),
        CourierInfo(3L, "Мария Сидорова", "ул. Центральная, 5")
    )

    @PostConstruct
    fun init() {
        createOrder(
            OrderRequest(
                deliveryAddress = "ул. Пушкина, 15, кв. 42",
                senderAddress = "ул. Гагарина, 7",
                recipientName = "Алексей Смирнов",
                weight = 5,
                deliveryType = "Стандартная"
            ),
            createdDate = LocalDateTime.now().minusDays(2)
        )

        createOrder(
            OrderRequest(
                deliveryAddress = "пр. Мира, 100, офис 301",
                senderAddress = "ул. Советская, 20",
                recipientName = "ООО Компания",
                weight = 15,
                deliveryType = "Экспресс"
            ),
            createdDate = LocalDateTime.now().minusDays(1)
        )

        createOrder(
            OrderRequest(
                deliveryAddress = "ул. Лесная, 33, дом 2",
                senderAddress = "ул. Молодежная, 8",
                recipientName = "Елена Петрова",
                weight = 3,
                deliveryType = "Стандартная"
            ),
            createdDate = LocalDateTime.now().minusHours(5)
        )

        // Назначаем курьера для первого заказа
        assignCourier(1L)
        // Начинаем доставку для первого заказа
        startDelivery(1L)
    }

    fun createOrder(req: OrderRequest, createdDate: LocalDateTime = LocalDateTime.now()): OrderResponse {
        val id = orderIdSequence.getAndIncrement()
        val response = OrderResponse(
            id = id,
            deliveryAddress = req.deliveryAddress,
            senderAddress = req.senderAddress,
            recipientName = req.recipientName,
            weight = req.weight,
            deliveryType = req.deliveryType,
            status = DeliveryStatus.PENDING,
            createdDate = createdDate,
            courierInfo = null,
            estimatedDeliveryTime = createdDate.plusHours(Random.nextLong(2, 8))
        )
        orders[id] = response
        return response
    }

    fun getOrderById(id: Long): OrderResponse? = orders[id]

    fun getAllOrders(): List<OrderResponse> = orders.values.toList()

    fun assignCourier(id: Long): OrderResponse? {
        val order = orders[id] ?: return null
        if (order.status != DeliveryStatus.PENDING) return order
        
        val courier = availableCouriers.random()
        val updatedOrder = order.copy(
            status = DeliveryStatus.ASSIGNED,
            courierInfo = courier
        )
        orders[id] = updatedOrder
        return updatedOrder
    }

    fun startDelivery(id: Long): OrderResponse? {
        val order = orders[id] ?: return null
        if (order.status != DeliveryStatus.ASSIGNED) return order
        
        val updatedOrder = order.copy(status = DeliveryStatus.IN_PROGRESS)
        orders[id] = updatedOrder
        return updatedOrder
    }

    fun completeDelivery(id: Long): OrderResponse? {
        val order = orders[id] ?: return null
        if (order.status != DeliveryStatus.IN_PROGRESS) return order
        
        val updatedOrder = order.copy(status = DeliveryStatus.DELIVERED)
        orders[id] = updatedOrder
        return updatedOrder
    }

    fun cancelOrder(id: Long): OrderResponse? {
        val order = orders[id] ?: return null
        val updatedOrder = order.copy(status = DeliveryStatus.CANCELLED)
        orders[id] = updatedOrder
        return updatedOrder
    }
}
