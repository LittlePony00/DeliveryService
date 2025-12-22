package com.nikita.main.assembler

import com.nikita.api.dto.OrderResponse
import com.nikita.main.contorller.DeliveryController
import org.springframework.hateoas.EntityModel
import org.springframework.hateoas.server.RepresentationModelAssembler
import org.springframework.hateoas.server.core.DummyInvocationUtils.methodOn
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo
import org.springframework.stereotype.Component

@Component
class OrderModelAssembler : RepresentationModelAssembler<OrderResponse, EntityModel<OrderResponse>> {
    override fun toModel(order: OrderResponse): EntityModel<OrderResponse> {
        return EntityModel.of(
            order,
            linkTo(methodOn(DeliveryController::class.java).getOrderById(order.id)).withSelfRel(),
            linkTo(methodOn(DeliveryController::class.java).getAllOrders(0, 10)).withRel("collection"),
        )
    }
}
