package grpc.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class GrpcClientApplication

fun main(args: Array<String>) {
    runApplication<GrpcClientApplication>(*args)
}
