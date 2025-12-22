package com.immortalidiot.audit

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.immortalidiot.audit"])
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
