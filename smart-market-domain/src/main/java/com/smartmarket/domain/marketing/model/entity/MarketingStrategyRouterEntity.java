package com.smartmarket.domain.marketing.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description 营销策略路由配置实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketingStrategyRouterEntity {

    /** 活动ID */
    private Long activityId;
    /** 用户分层 */
    private String userSegment;
    /** 风险等级 */
    private String riskLevel;
    /** 路由后的策略ID */
    private Long strategyId;
    /** 优先级，数值越小优先级越高 */
    private Integer priority;
    /** 是否启用 */
    private Integer enabled;
    /** 路由描述 */
    private String routerDesc;

}
