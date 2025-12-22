package com.nikita.events

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.serialization.Serializable

@Serializable
sealed interface RatedEvent : java.io.Serializable {
    data class CourierRatedEvent @JsonCreator constructor(
        @JsonProperty("courierId") val courierId: String,
        @JsonProperty("orderId") val orderId: Long,
        @JsonProperty("score") val score: Int,
        @JsonProperty("verdict") val verdict: String
    ) : RatedEvent
}
