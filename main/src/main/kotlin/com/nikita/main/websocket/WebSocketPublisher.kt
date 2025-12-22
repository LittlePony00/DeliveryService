package com.nikita.main.websocket

import com.fasterxml.jackson.databind.ObjectMapper
import com.nikita.events.events.Event
import org.springframework.stereotype.Service

@Service
class DeliveryWebSocketPublisher(
    private val handler: DeliveryWebSocketHandler,
    private val objectMapper: ObjectMapper
) {

    fun sendEvent(event: Event) {
        val json = objectMapper.writeValueAsString(event)
        handler.broadcast(json)
    }
}
