package com.smartmarket.trigger.job;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.GetResponse;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description RabbitMQ死信队列补偿任务
 * @create 2026-06-30
 */
@Slf4j
@Component
public class RabbitDeadLetterCompensateJob {

    private static final String ORIGINAL_ROUTING_KEY_HEADER = "x-original-routing-key";

    @Resource
    private RabbitTemplate rabbitTemplate;

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
    @Value("${spring.rabbitmq.dlq.compensate.max-messages-per-queue:10}")
    private int maxMessagesPerQueue;

    @XxlJob("RabbitDeadLetterCompensateJob")
    public void exec() {
        List<String> businessQueues = Arrays.asList(activitySkuStockZeroQueue, sendAwardQueue, sendRebateQueue, creditAdjustSuccessQueue);
        for (String businessQueue : businessQueues) {
            compensateQueue(businessQueue + deadLetterSuffix, businessQueue);
        }
    }

    private void compensateQueue(String deadLetterQueue, String defaultRoutingKey) {
        for (int i = 0; i < maxMessagesPerQueue; i++) {
            Boolean compensated = rabbitTemplate.execute(channel -> {
                GetResponse response = channel.basicGet(deadLetterQueue, false);
                if (null == response) {
                    return false;
                }

                long deliveryTag = response.getEnvelope().getDeliveryTag();
                String routingKey = parseOriginalRoutingKey(response.getProps(), defaultRoutingKey);
                try {
                    channel.basicPublish("", routingKey, response.getProps(), response.getBody());
                    channel.basicAck(deliveryTag, false);
                    log.info("RabbitMQ死信消息补偿成功 deadLetterQueue: {} routingKey: {} messageId: {}", deadLetterQueue, routingKey, response.getProps().getMessageId());
                    return true;
                } catch (Exception e) {
                    channel.basicNack(deliveryTag, false, true);
                    log.error("RabbitMQ死信消息补偿失败，消息已保留在死信队列 deadLetterQueue: {} routingKey: {}", deadLetterQueue, routingKey, e);
                    throw e;
                }
            });
            if (!Boolean.TRUE.equals(compensated)) {
                return;
            }
        }
    }

    private String parseOriginalRoutingKey(AMQP.BasicProperties properties, String defaultRoutingKey) {
        Map<String, Object> headers = properties.getHeaders();
        if (null == headers) {
            return defaultRoutingKey;
        }

        Object originalRoutingKey = headers.get(ORIGINAL_ROUTING_KEY_HEADER);
        if (null == originalRoutingKey) {
            return defaultRoutingKey;
        }

        String routingKey = String.valueOf(originalRoutingKey);
        if (routingKey.trim().isEmpty()) {
            return defaultRoutingKey;
        }

        return routingKey;
    }

}


