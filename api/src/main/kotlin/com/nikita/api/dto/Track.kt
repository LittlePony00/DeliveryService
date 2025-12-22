package com.nikita.api.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.hateoas.RepresentationModel
import org.springframework.hateoas.server.core.Relation
import java.time.LocalDateTime

data class OrderRequest(
    @NotBlank val deliveryAddress: String,
    @NotBlank val senderAddress: String,
    @NotBlank val recipientName: String,
    @Min(1) val weight: Int,
    @NotBlank val deliveryType: String,
)

data class OrderStatusChangeRequest(
    @NotNull val orderId: Long,
    @NotNull val status: DeliveryStatus,
)

data class CourierInfo(
    val courierId: Long?,
    val courierName: String?,
    val currentLocation: String?
)

@Relation(collectionRelation = "orders", itemRelation = "order")
data class OrderResponse(
    val id: Long,
    val deliveryAddress: String,
    val senderAddress: String,
    val recipientName: String,
    val weight: Int,
    val deliveryType: String,
    var status: DeliveryStatus,
    val createdDate: LocalDateTime,
    val courierInfo: CourierInfo? = null,
    val estimatedDeliveryTime: LocalDateTime? = null
) : RepresentationModel<OrderResponse>()
