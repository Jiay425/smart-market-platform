package com.smartmarket.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ listener retry exhausted messages are republished to a DLQ.
 */
@Slf4j
@Configuration
public class RabbitDeadLetterConfig {

    @Value("${spring.rabbitmq.topic.activity_sku_stock_zero}")
    private String activitySkuStockZeroQueue;
    @Value("${spring.rabbitmq.topic.send_award}")
    private String sendAwardQueue;
    @Value("${spring.rabbitmq.topic.send_rebate}")
    private String sendRebateQueue;
    @Value("${spring.rabbitmq.topic.credit_adjust_success}")
    private String creditAdjustSuccessQueue;
    @Value("${spring.rabbitmq.topic.dead_letter_suffix:.dlq}")
    private String deadLetterSuffix;

    @Bean
    public Queue activitySkuStockZeroDeadLetterQueue() {
        return QueueBuilder.durable(activitySkuStockZeroQueue + deadLetterSuffix).build();
    }

    @Bean
    public Queue sendAwardDeadLetterQueue() {
        return QueueBuilder.durable(sendAwardQueue + deadLetterSuffix).build();
    }

    @Bean
    public Queue sendRebateDeadLetterQueue() {
        return QueueBuilder.durable(sendRebateQueue + deadLetterSuffix).build();
    }

    @Bean
    public Queue creditAdjustSuccessDeadLetterQueue() {
        return QueueBuilder.durable(creditAdjustSuccessQueue + deadLetterSuffix).build();
    }

    @Bean
    public MessageRecoverer deadLetterMessageRecoverer(RabbitTemplate rabbitTemplate) {
        return (message, cause) -> {
            MessageProperties messageProperties = message.getMessageProperties();
            String sourceQueue = messageProperties.getReceivedRoutingKey();
            if (null == sourceQueue || sourceQueue.trim().isEmpty()) {
                sourceQueue = messageProperties.getConsumerQueue();
            }
            String deadLetterQueue = sourceQueue + deadLetterSuffix;
            messageProperties.setHeader("x-exception-message", cause.getMessage());
            messageProperties.setHeader("x-original-routing-key", sourceQueue);
            rabbitTemplate.send("", deadLetterQueue, message);
            log.error("RabbitMQ消息重试耗尽，已投递死信队列 sourceQueue: {} deadLetterQueue: {}", sourceQueue, deadLetterQueue, cause);
        };
    }

}


