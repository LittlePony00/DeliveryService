package com.immortalidiot.main.websocket

import com.fasterxml.jackson.databind.ObjectMapper
import com.immortalidiot.events.Event
import org.springframework.stereotype.Service

@Service
class TrackWebSocketPublisher(
    private val handler: TrackWebSocketHandler,
    private val objectMapper: ObjectMapper
) {

    fun sendEvent(event: Event) {
        val json = objectMapper.writeValueAsString(event)
        handler.broadcast(json)
    }
}
