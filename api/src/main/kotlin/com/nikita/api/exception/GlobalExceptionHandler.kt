package com.nikita.api.exception

import com.nikita.api.dto.StatusResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler

class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleNotFound(resourceNotFoundException: ResourceNotFoundException): ResponseEntity<StatusResponse> =
        ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(StatusResponse("error", resourceNotFoundException.message ?: "Unknown"))

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(methodArgumentNotValidException: MethodArgumentNotValidException): ResponseEntity<StatusResponse> {
        val message = methodArgumentNotValidException.fieldErrors
            .joinToString("; ") { "${it.field} - ${it.defaultMessage ?: "Unknown"}" }

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(StatusResponse("error", "Validation error: $message"))
    }
}