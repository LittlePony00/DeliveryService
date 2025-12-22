package com.immortalidiot.main.contorller

import com.immortalidiot.RatedEvent.CourierRatedEvent
import com.immortalidiot.main.AnalyticsServiceGrpc
import com.immortalidiot.main.CourierRatingRequest
import com.immortalidiot.main.config.RabbitMQConfig
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


@RestController
class RatingController(
    private val analyticsStub: AnalyticsServiceGrpc.AnalyticsServiceBlockingStub,
    private val rabbitTemplate: RabbitTemplate
) {

    @PostMapping("/api/couriers/{courierId}/rate")
    fun rateCourier(
        @PathVariable courierId: Long,
        @RequestParam orderId: Long
    ): String {
        val request = CourierRatingRequest.newBuilder()
            .setCourierId(courierId)
            .setOrderId(orderId)
            .setCategory("Delivery")
            .build()
        val gRpcResponse = analyticsStub.calculateCourierRating(request)

        val event = CourierRatedEvent(
            courierId = gRpcResponse.courierId.toString(),
            orderId = orderId,
            score = gRpcResponse.ratingScore,
            verdict = gRpcResponse.verdict
        )

        rabbitTemplate.convertAndSend(RabbitMQConfig.FANOUT_EXCHANGE, "", event)

        return "Courier rating calculated: " + gRpcResponse.ratingScore
    }
}
