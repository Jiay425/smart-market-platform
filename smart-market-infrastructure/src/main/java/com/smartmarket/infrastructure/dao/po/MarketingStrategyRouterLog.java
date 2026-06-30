package com.smartmarket.infrastructure.dao.po;

import lombok.Data;

import java.util.Date;

@Data
public class MarketingStrategyRouterLog {

    private Long id;
    private String userId;
    private Long activityId;
    private Long defaultStrategyId;
    private Long routedStrategyId;
    private String userSegment;
    private String riskLevel;
    private String routeDesc;
    private Date createTime;

}
