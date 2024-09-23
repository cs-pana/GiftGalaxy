package com.example.profileservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.support.converter.MessageConverter;

@Configuration
public class RabbitMQConfig {

   
    @Bean
    public Queue profileUpdateQueue() {
        return new Queue("profile.update.queue", true);
    }

     @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();  // Use Jackson JSON converter
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());  // Set JSON converter for RabbitTemplate
        return rabbitTemplate;
    }

}