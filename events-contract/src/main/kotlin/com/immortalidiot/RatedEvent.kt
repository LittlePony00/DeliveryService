package com.immortalidiot

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.serialization.Serializable

@Serializable
sealed interface RatedEvent : java.io.Serializable {
    data class UserRatedEvent @JsonCreator constructor(
        @JsonProperty("userId") val userId: String,
        @JsonProperty("score") val score: Int,
        @JsonProperty("verdict") val verdict: String
    ) : RatedEvent
}
