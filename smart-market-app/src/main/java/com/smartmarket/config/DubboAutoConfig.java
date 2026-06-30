package com.smartmarket.config;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableDubbo
@ConditionalOnProperty(prefix = "perf.dubbo", name = "enabled", havingValue = "true", matchIfMissing = true)
public class DubboAutoConfig {
}


