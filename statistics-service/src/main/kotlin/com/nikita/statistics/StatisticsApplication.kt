package com.nikita.statistics

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.nikita.statistics", "com.nikita.events"])
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}