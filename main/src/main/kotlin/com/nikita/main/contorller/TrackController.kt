package com.immortalidiot.main.contorller

import com.immortalidiot.api.DeliveryApi
import com.immortalidiot.api.dto.OrderRequest
import com.immortalidiot.api.dto.OrderResponse
import com.immortalidiot.main.assembler.OrderModelAssembler
import com.immortalidiot.main.service.DeliveryService
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.web.PagedResourcesAssembler
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.PagedModel
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RestController

@RestController
class DeliveryController(
    private val deliveryService: DeliveryService,
    private val orderModelAssembler: OrderModelAssembler,
    private val pagedResourceAssembler: PagedResourcesAssembler<OrderResponse>
) : DeliveryApi {

    override fun createOrder(orderRequest: OrderRequest): ResponseEntity<EntityModel<OrderResponse>> {
        val order = deliveryService.createOrder(orderRequest)
        val entityModel = orderModelAssembler.toModel(order)

        return ResponseEntity
            .created(entityModel.getRequiredLink("self").toUri())
            .body(entityModel)
    }

    override fun getOrderById(id: Long): EntityModel<OrderResponse> {
        val order = deliveryService.getOrderById(id)
        return orderModelAssembler.toModel(order)
    }

    override fun getAllOrders(page: Int, size: Int): PagedModel<EntityModel<OrderResponse>> {
        val orderPage = with(deliveryService.getAllOrders(page, size)) {
            PageImpl(
                content,
                PageRequest.of(pageNumber, pageSize),
                totalElements
            )
        }

        return pagedResourceAssembler.toModel(orderPage, orderModelAssembler)
    }

    override fun assignCourier(id: Long): EntityModel<OrderResponse> {
        val order = deliveryService.assignCourier(id)
        return orderModelAssembler.toModel(order)
    }

    override fun startDelivery(id: Long): EntityModel<OrderResponse> {
        val order = deliveryService.startDelivery(id)
        return orderModelAssembler.toModel(order)
    }

    override fun completeDelivery(id: Long): EntityModel<OrderResponse> {
        val order = deliveryService.completeDelivery(id)
        return orderModelAssembler.toModel(order)
    }

    override fun cancelOrder(id: Long) {
        deliveryService.cancelOrder(id)
    }
}
