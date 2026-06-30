package com.smartmarket.infrastructure.dao.po;

import lombok.Data;

import java.util.Date;

@Data
public class MarketingStrategyRouter {

    private Long id;
    private Long activityId;
    private String userSegment;
    private String riskLevel;
    private Long strategyId;
    private Integer priority;
    private Integer enabled;
    private String routerDesc;
    private Date createTime;
    private Date updateTime;

}
