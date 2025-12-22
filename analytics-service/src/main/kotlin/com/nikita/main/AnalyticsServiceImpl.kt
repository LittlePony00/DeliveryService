package com.immortalidiot.main

import io.grpc.stub.StreamObserver
import org.springframework.grpc.server.service.GrpcService

@GrpcService
class AnalyticsServiceImpl : AnalyticsServiceGrpc.AnalyticsServiceImplBase() {

    override fun calculateCourierRating(
        request: CourierRatingRequest,
        responseObserver: StreamObserver<CourierRatingResponse>
    ) {
        // Симуляция расчета рейтинга курьера на основе истории доставок
        val score = (Math.random() * 100).toInt()
        val verdict = when {
            score >= 80 -> "EXCELLENT"
            score >= 60 -> "GOOD"
            score >= 40 -> "AVERAGE"
            else -> "NEEDS_IMPROVEMENT"
        }

        val response = CourierRatingResponse.newBuilder()
            .setCourierId(request.courierId)
            .setRatingScore(score)
            .setVerdict(verdict)
            .build()

        println("📊 Analytics: Calculated rating for courier ${request.courierId}: $score ($verdict)")

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }
}
