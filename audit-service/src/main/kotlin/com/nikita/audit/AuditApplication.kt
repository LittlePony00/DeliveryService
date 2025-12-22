package com.nikita.audit

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.nikita.audit"])
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
