package com.immortalidiot.statistics

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.immortalidiot.statistics"])
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}