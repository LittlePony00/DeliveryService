package com.immortalidiot.main.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.core.FanoutExchange
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMQConfig {

    @Bean
    fun tracksExchange() : TopicExchange {
        return TopicExchange(EXCHANGE_NAME, true, false)
    }

    @Bean
    fun messageConverter() : Jackson2JsonMessageConverter {
        return Jackson2JsonMessageConverter(ObjectMapper().findAndRegisterModules())
    }

    @Bean
    fun analyticsExchange(): FanoutExchange {
        return FanoutExchange(FANOUT_EXCHANGE, true, false)
    }

    @Bean
    fun rabbitTemplate(
        connectionFactory: ConnectionFactory,
        messageConverter: Jackson2JsonMessageConverter
    ): RabbitTemplate {
        val template = RabbitTemplate(connectionFactory)
        template.messageConverter = messageConverter
        template.setConfirmCallback { _, ack, cause ->
            if (!ack) {
                println("NACK: Message delivered failed! $cause")
            }
        }

        return template
    }

    companion object {
        const val EXCHANGE_NAME: String = "tracks-exchange"
        const val ROUTING_KEY_TRACK_CREATED: String = "track.created"
        const val ROUTING_KEY_TRACK_DELETED: String = "track.deleted"

        const val FANOUT_EXCHANGE: String = "analytics-fanout"
    }
}
