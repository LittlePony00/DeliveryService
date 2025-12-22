package com.nikita.statistics.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.core.AcknowledgeMode
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitConfig {

    @Bean
    fun messageConverter(): Jackson2JsonMessageConverter {
        return Jackson2JsonMessageConverter(ObjectMapper().findAndRegisterModules())
    }

    @Bean
    fun statisticsQueue() = Queue(QUEUE_NAME, true)

    @Bean
    fun statisticsCompletedQueue() = Queue(QUEUE_NAME_COMPLETED, true)

    @Bean
    fun statisticsExchange() = TopicExchange(EXCHANGE_NAME)

    @Bean
    fun bindingStatisticsQueue(
        @Qualifier("statisticsQueue") queue: Queue,
        exchange: TopicExchange
    ) = BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY_ORDER_CREATED)

    @Bean
    fun bindingStatisticsCompletedQueue(
        @Qualifier("statisticsCompletedQueue") statisticsCompletedQueue: Queue,
        exchange: TopicExchange
    ) = BindingBuilder.bind(statisticsCompletedQueue).to(exchange).with(ROUTING_KEY_DELIVERY_COMPLETED)

    @Bean
    fun containerFactory(connectionFactory: ConnectionFactory): SimpleRabbitListenerContainerFactory {
        val factory = SimpleRabbitListenerContainerFactory()

        factory.setConnectionFactory(connectionFactory)
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL)

        return factory
    }

    companion object {
        internal const val QUEUE_NAME: String = "statistics-queue"
        internal const val QUEUE_NAME_COMPLETED: String = "statistics-completed-queue"
        internal const val EXCHANGE_NAME: String = "delivery-exchange"
        internal const val ROUTING_KEY_ORDER_CREATED: String = "order.created"
        internal const val ROUTING_KEY_DELIVERY_COMPLETED: String = "delivery.completed"
    }
}