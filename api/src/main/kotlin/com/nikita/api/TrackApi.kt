package com.immortalidiot.api

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import com.immortalidiot.api.dto.OrderRequest
import com.immortalidiot.api.dto.OrderResponse
import com.immortalidiot.api.dto.StatusResponse
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.PagedModel
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "orders", description = "API для управления заказами доставки")
@ApiResponse(
    responseCode = "400",
    description = "Ошибка валидации",
    content = [Content(mediaType = "application/json", schema = Schema(implementation = StatusResponse::class))]
)
@ApiResponse(
    responseCode = "500",
    description = "Внутренняя ошибка",
    content = [Content(mediaType = "application/json", schema = Schema(implementation = StatusResponse::class))]
)
@RequestMapping("/api/orders")
interface DeliveryApi {

    @Operation(summary = "Создать новый заказ на доставку")
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    @ApiResponse(responseCode = "201", description = "Заказ успешно создан")
    fun createOrder(@Valid @RequestBody orderRequest: OrderRequest): ResponseEntity<EntityModel<OrderResponse>>

    @Operation(summary = "Получить заказ по ID")
    @GetMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "Заказ найден")
    fun getOrderById(@PathVariable id: Long): EntityModel<OrderResponse>

    @Operation(summary = "Получить все заказы")
    @GetMapping
    @ApiResponse(responseCode = "200", description = "Заказы найдены")
    fun getAllOrders(
        @Parameter(description = "Номер страницы (0..N)")
        @RequestParam(defaultValue = "0")
        page: Int,
        @Parameter(description = "Размер страницы")
        @RequestParam(defaultValue = "10")
        size: Int,
    ): PagedModel<EntityModel<OrderResponse>>

    @Operation(summary = "Назначить курьера на заказ")
    @PostMapping("/{id}/assign-courier")
    @ApiResponse(responseCode = "200", description = "Курьер назначен")
    fun assignCourier(@PathVariable id: Long): EntityModel<OrderResponse>

    @Operation(summary = "Начать доставку")
    @PostMapping("/{id}/start-delivery")
    @ApiResponse(responseCode = "200", description = "Доставка начата")
    fun startDelivery(@PathVariable id: Long): EntityModel<OrderResponse>

    @Operation(summary = "Завершить доставку")
    @PostMapping("/{id}/complete-delivery")
    @ApiResponse(responseCode = "200", description = "Доставка завершена")
    fun completeDelivery(@PathVariable id: Long): EntityModel<OrderResponse>

    @Operation(summary = "Отменить заказ")
    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204", description = "Заказ отменен")
    @ApiResponse(responseCode = "404", description = "Заказ не найден")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun cancelOrder(@PathVariable id: Long)
}
