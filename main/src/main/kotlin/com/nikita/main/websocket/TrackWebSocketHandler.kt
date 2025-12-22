package com.nikita.main.websocket

import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.concurrent.CopyOnWriteArraySet

@Component
class DeliveryWebSocketHandler : TextWebSocketHandler() {

    private val sessions = CopyOnWriteArraySet<WebSocketSession>()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        sessions.add(session)
        println("WS connected (Delivery Tracking): ${session.id}")
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        sessions.remove(session)
        println("WS disconnected (Delivery Tracking): ${session.id}")
    }

    fun broadcast(message: String) {
        sessions.forEach {
            if (it.isOpen) {
                it.sendMessage(TextMessage(message))
            }
        }
    }
}
