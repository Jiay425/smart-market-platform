package com.smartmarket.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * RabbitMQ publisher return logging.
 */
@Slf4j
@Configuration
public class RabbitPublisherConfig {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() {
        rabbitTemplate.setReturnsCallback(returned -> log.error(
                "RabbitMQ消息路由失败 replyCode: {} replyText: {} exchange: {} routingKey: {} message: {}",
                returned.getReplyCode(),
                returned.getReplyText(),
                returned.getExchange(),
                returned.getRoutingKey(),
                new String(returned.getMessage().getBody())
        ));
    }

}


