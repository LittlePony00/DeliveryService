package com.nikita.main.graphql

import com.nikita.api.dto.OrderRequest
import com.nikita.api.dto.OrderResponse
import com.netflix.graphql.dgs.*
import graphql.schema.DataFetchingEnvironment
import com.nikita.main.service.DeliveryService

@DgsComponent
class DeliveryDataFetcher(private val deliveryService: DeliveryService) {
    @DgsQuery
    fun orderById(@InputArgument id: Long) = deliveryService.getOrderById(id)

    @DgsQuery
    fun orders(@InputArgument page: Int = 0, @InputArgument size: Int = 10) =
        deliveryService.getAllOrders(page, size)

    @DgsMutation
    fun createOrder(@InputArgument("input") input: Map<String, Any?>) = deliveryService.createOrder(
        orderRequest = OrderRequest(
            deliveryAddress = input.getValue("deliveryAddress").toString(),
            senderAddress = input.getValue("senderAddress").toString(),
            recipientName = input.getValue("recipientName").toString(),
            weight = input.getValue("weight").toString().toInt(),
            deliveryType = input.getValue("deliveryType").toString()
        )
    )

    @DgsMutation
    fun assignCourier(@InputArgument id: Long) = deliveryService.assignCourier(id)

    @DgsMutation
    fun startDelivery(@InputArgument id: Long) = deliveryService.startDelivery(id)

    @DgsMutation
    fun completeDelivery(@InputArgument id: Long) = deliveryService.completeDelivery(id)

    @DgsMutation
    fun cancelOrder(@InputArgument id: Long): Long {
        deliveryService.cancelOrder(id)
        return id
    }

    @DgsData(parentType = "Order", field = "recipientName")
    fun recipientName(dfe: DataFetchingEnvironment) = dfe.getSource<OrderResponse>()!!.recipientName
}

