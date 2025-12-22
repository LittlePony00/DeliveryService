package com.nikita.main

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.grpc.client.ImportGrpcClients
import org.springframework.hateoas.config.EnableHypermediaSupport

@SpringBootApplication(
    scanBasePackages = ["com.nikita.main", "com.nikita.api", "com.nikita.events"],
    exclude = [DataSourceAutoConfiguration::class]
)
@EnableHypermediaSupport(type = [EnableHypermediaSupport.HypermediaType.HAL])
@ImportGrpcClients
class DeliveryApplication

fun main(args: Array<String>) {
    runApplication<DeliveryApplication>(*args)
}
