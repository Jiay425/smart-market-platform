package com.smartmarket.domain.marketing.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description 营销策略路由请求
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketingRouteRequestEntity {

    /** 用户ID */
    private String userId;
    /** 活动ID */
    private Long activityId;
    /** 活动默认策略ID */
    private Long defaultStrategyId;

}
