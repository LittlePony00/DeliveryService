package com.immortalidiot.main

import io.grpc.stub.StreamObserver
import org.springframework.grpc.server.service.GrpcService

@GrpcService
class AnalyticsServiceImpl : AnalyticsServiceGrpc.AnalyticsServiceImplBase() {

    override fun calculateUserRating(
        request: UserRatingRequest,
        responseObserver: StreamObserver<UserRatingResponse>
    ) {
        val score = (Math.random() * 100).toInt()
        val verdict = if (score > 50) "GOOD" else "BAD"

        val response = UserRatingResponse.newBuilder()
            .setUserId(request.userId)
            .setRatingScore(score)
            .setVerdict(verdict)
            .build()

        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }
}
