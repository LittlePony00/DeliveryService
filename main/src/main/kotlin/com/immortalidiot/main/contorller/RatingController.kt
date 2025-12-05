package com.immortalidiot.main.contorller

import com.immortalidiot.RatedEvent.UserRatedEvent
import com.immortalidiot.main.AnalyticsServiceGrpc
import com.immortalidiot.main.UserRatingRequest
import com.immortalidiot.main.config.RabbitMQConfig
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class RatingController(
    private val analyticsStub: AnalyticsServiceGrpc.AnalyticsServiceBlockingStub,
    private val rabbitTemplate: RabbitTemplate
) {

    @PostMapping("/api/users/{id}/rate")
    fun rateUser(@PathVariable id: Long): String {
        val request = UserRatingRequest.newBuilder().setUserId(id).setCategory("General").build()
        val gRpcResponse = analyticsStub.calculateUserRating(request)

        val event = UserRatedEvent(
            userId = gRpcResponse.userId.toString(),
            score = gRpcResponse.ratingScore,
            verdict = gRpcResponse.verdict
        )

        rabbitTemplate.convertAndSend(RabbitMQConfig.FANOUT_EXCHANGE, "", event)

        return "Rating calculated: " + gRpcResponse.ratingScore
    }
}
