package com.nikita.events.events

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

sealed interface DeliveryEvent : Event {
    data class OrderCreatedEvent @JsonCreator constructor(
        @JsonProperty("orderId") val orderId: Long,
        @JsonProperty("deliveryAddress") val deliveryAddress: String,
        @JsonProperty("senderAddress") val senderAddress: String,
        @JsonProperty("recipientName") val recipientName: String
    ) : DeliveryEvent

    data class CourierAssignedEvent @JsonCreator constructor(
        @JsonProperty("orderId") val orderId: Long,
        @JsonProperty("courierId") val courierId: Long,
        @JsonProperty("courierName") val courierName: String
    ) : DeliveryEvent

    data class DeliveryStartedEvent @JsonCreator constructor(
        @JsonProperty("orderId") val orderId: Long,
        @JsonProperty("courierId") val courierId: Long,
        @JsonProperty("currentLocation") val currentLocation: String
    ) : DeliveryEvent

    data class DeliveryCompletedEvent @JsonCreator constructor(
        @JsonProperty("orderId") val orderId: Long,
        @JsonProperty("courierId") val courierId: Long
    ) : DeliveryEvent

    @JsonIgnoreProperties(ignoreUnknown = true)
    data class DeliveryCancelledEvent @JsonCreator constructor(
        @JsonProperty("orderId") val orderId: Long,
        @JsonProperty("reason") val reason: String? = null
    ) : DeliveryEvent
}
